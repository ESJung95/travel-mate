package com.eunsun.travel_mate.service.tourinfo;

import com.eunsun.travel_mate.domain.AreaCode;
import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import com.eunsun.travel_mate.domain.tourInfo.TourInfoDocument;
import com.eunsun.travel_mate.repository.elasticsearch.TourInfoDocumentRepository;
import com.eunsun.travel_mate.repository.jpa.AreaCodeRepository;
import com.eunsun.travel_mate.repository.jpa.TourInfoRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourInfoService {

  @Value("${tour.openapi.key}")
  private String apiKey;

  private final AreaCodeRepository areaCodeRepository;
  private final TourInfoRepository tourInfoRepository;
  private final TourInfoDocumentRepository tourInfoDocumentRepository;
  private final ElasticsearchOperations elasticsearchOperations;

  @Transactional
  @Scheduled(cron = "0 0 0 1 * *") // 매월 1일에 실행
  public void updateTourInfoMonthly() {
    List<AreaCode> areaCodes = areaCodeRepository.findAll();

    for (AreaCode areaCode : areaCodes) {
      String code = areaCode.getCode();
      int totalCount = getTotalCount(code);
      int numOfRows = 1000;
      int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

      for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
        String tourInfoString = getTourInfoString(code, pageNo, numOfRows);
        List<TourInfo> newTourInfoList = parseTourInfo(tourInfoString, areaCode);

        for (TourInfo newTourInfo : newTourInfoList) {
          Optional<TourInfo> existingTourInfo = tourInfoRepository.findById(newTourInfo.getTourInfoId());

          if (existingTourInfo.isPresent()) {
            TourInfo tourInfo = existingTourInfo.get();
            String existingModifiedTime = tourInfo.getModifiedTime();
            String newModifiedTime = newTourInfo.getModifiedTime();

            if (!existingModifiedTime.equals(newModifiedTime)) {
              TourInfo updatedTourInfo = tourInfo.update(newTourInfo);
              tourInfoRepository.save(updatedTourInfo);

              IndexQuery indexQuery = new IndexQueryBuilder()
                  .withId(tourInfo.getTourInfoId().toString())
                  .withObject(TourInfoDocument.from(updatedTourInfo))
                  .build();
              elasticsearchOperations.index(indexQuery, IndexCoordinates.of("tour_info"));
            }
          } else {
            tourInfoRepository.save(newTourInfo);
            IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(newTourInfo.getTourInfoId().toString())
                .withObject(TourInfoDocument.from(newTourInfo))
                .build();
            elasticsearchOperations.index(indexQuery, IndexCoordinates.of("tour_info"));
          }
        }

        log.info("AreaCode: {}, Page: {}/{} 업데이트 및 인덱싱 완료", code, pageNo, totalPages);
      }

      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        log.warn("지연 시간 추가 실패", e);
      }
    }
    log.info("모든 여행 정보 데이터 업데이트 완료");
  }

  // 지역별 여행 정보 가져와서 DB에 저장하기
  @Transactional
  public void getTourInfoFromApi() {

    List<AreaCode> areaCodes = areaCodeRepository.findAll();

    for (AreaCode areaCode : areaCodes) {
      String code = areaCode.getCode();
      int totalCount = getTotalCount(code);
      int numOfRows = 1000;
      int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

      // 데이터 가져오기
      for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
        String tourInfoString = getTourInfoString(code, pageNo, numOfRows);

        // 파싱하기
        List<TourInfo> tourInfoList = parseTourInfo(tourInfoString, areaCode);

        // Entity 저장하기
        tourInfoRepository.saveAll(tourInfoList);

        // Elasticsearch 인덱싱
        List<IndexQuery> indexQueries = tourInfoList.stream()
            .map(tourInfo -> new IndexQueryBuilder()
                .withId(tourInfo.getTourInfoId().toString())
                .withObject(TourInfoDocument.from(tourInfo))
                .build())
            .collect(Collectors.toList());
        elasticsearchOperations.bulkIndex(indexQueries, IndexCoordinates.of("tour_info"));

        log.info("AreaCode: {}, Page: {}/{} 저장 및 인덱싱 완료", code, pageNo, totalPages);
    }

      // 지연 시간 추가
      try {
        Thread.sleep(500); // 0.5초 대기
      } catch (InterruptedException e) {
        log.warn("지연 시간 추가 실패", e);
      }
    }
    log.info("모든 여행 정보 데이터 저장 완료");
  }

  // OpenApi 에서 TourInfo 데이터 가져오기
  public String getTourInfoString(String areaCode, int pageNo, int numOfRows) {

    String apiUrl = "https://apis.data.go.kr/B551011/KorService1/areaBasedList1?MobileOS=ETC&MobileApp=TEST&_type=json&listYN=Y&serviceKey="
        + apiKey + "&areaCode=" + areaCode + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;

    try {
      URL url = new URL(apiUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      int responseCode = connection.getResponseCode();

      BufferedReader br;
      if (responseCode == 200) {
        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } else {
        br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
      }
      String inputLine;

      StringBuilder response = new StringBuilder();
      while ((inputLine = br.readLine()) != null) {
        response.append(inputLine);
      }

      br.close();
      return response.toString();

    } catch (Exception e) {
      log.error("여행 정보 데이터 가져오기 실패", e);
      throw new RuntimeException("여행 정보 데이터 가져오기 실패", e);
    }
  }

  // 데이터 파싱하기
  public List<TourInfo> parseTourInfo(String jsonString, AreaCode areaCode) {
    List<TourInfo> tourInfoList = new ArrayList<>();

    JSONParser jsonParser = new JSONParser();

    try {
      JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
      JSONObject response = (JSONObject) jsonObject.get("response");
      JSONObject body = (JSONObject) response.get("body");
      JSONObject items = (JSONObject) body.get("items");
      JSONArray itemArray = (JSONArray) items.get("item");

      for (Object item : itemArray) {
        JSONObject itemObject = (JSONObject) item;
        TourInfo tourInfo = TourInfo.builder()
            .areaCode(areaCode)
            .sigunguCode((String) itemObject.get("sigungucode"))
            .title((String) itemObject.get("title"))
            .addr1((String) itemObject.get("addr1"))
            .addr2((String) itemObject.get("addr2"))
            .contentTypeId((String) itemObject.get("contenttypeid"))
            .contentId((String) itemObject.get("contentid"))
            .mapx((String) itemObject.get("mapx"))
            .mapy((String) itemObject.get("mapy"))
            .imageUrl1((String) itemObject.get("firstimage"))
            .imageUrl2((String) itemObject.get("firstimage2"))
            .createdTime((String) itemObject.get("createdtime"))
            .modifiedTime((String) itemObject.get("modifiedtime"))
            .build();

        tourInfoList.add(tourInfo);
      }

    } catch (ParseException e) {
      log.error("TourInfo 데이터 파싱 실패", e);
      throw new RuntimeException("TourInfo 데이터 파싱 실패", e);
    }

    return tourInfoList;
  }

  // 전체 데이터 개수 가져오기
  public int getTotalCount(String areaCode) {
    String apiUrl = "https://apis.data.go.kr/B551011/KorService1/areaBasedList1?MobileOS=ETC&MobileApp=TEST&_type=json&listYN=N&serviceKey="
        + apiKey + "&areaCode=" + areaCode;

    try {
      URL url = new URL(apiUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      int responseCode = connection.getResponseCode();

      BufferedReader br;
      if (responseCode == 200) {
        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } else {
        br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
      }
      String inputLine;

      StringBuilder response = new StringBuilder();
      while ((inputLine = br.readLine()) != null) {
        response.append(inputLine);
      }

      br.close();

      JSONParser jsonParser = new JSONParser();
      JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());
      JSONObject parseResponse = (JSONObject) jsonObject.get("response");
      JSONObject parseBody = (JSONObject) parseResponse.get("body");
      long totalCount = (long) parseBody.get("totalCount");

      return (int) totalCount;

    } catch (Exception e) {
      log.error("전체 데이터 개수 가져오기 실패", e);
      throw new RuntimeException("전체 데이터 개수 가져오기 실패", e);
    }
  }

  // 여행지명을 검색
  public List<TourInfoDocument> searchByTitle(String keyword) {
    return tourInfoDocumentRepository.findByTitleContaining(keyword);
  }

  // 주소로 검색
  public List<TourInfoDocument> searchByAddress(String addr) {
    return tourInfoDocumentRepository.findByAddr1ContainingOrAddr2Containing(addr, addr);
  }
}