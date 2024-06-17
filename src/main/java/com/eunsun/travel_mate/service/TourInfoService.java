package com.eunsun.travel_mate.service;

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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class TourInfoService {

  @Value("${tour.openapi.key}")
  private String apiKey;

  @Value("${naver.cloud.geocoding.url}")
  private String geocodingUrl;

  @Value("${naver.cloud.geocoding.client-id}")
  private String clientId;

  @Value("${naver.cloud.geocoding.client-secret}")
  private String clientSecret;

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
          TourInfo existingTourInfo = tourInfoRepository.findById(newTourInfo.getTourInfoId())
              .orElseThrow(() -> new RuntimeException("여행 정보를 찾을 수 없습니다."));

          String existingModifiedTime = existingTourInfo.getModifiedTime();
          String newModifiedTime = newTourInfo.getModifiedTime();

          // ModifiedTime 이 변경된 경우만 update
          if (!existingModifiedTime.equals(newModifiedTime)) {
            TourInfo updatedTourInfo = existingTourInfo.update(newTourInfo);
            tourInfoRepository.save(updatedTourInfo);

            IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(updatedTourInfo.getTourInfoId().toString())
                .withObject(TourInfoDocument.from(updatedTourInfo))
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
        String mapx = (String) itemObject.get("mapx");
        String mapy = (String) itemObject.get("mapy");

        // 위도, 경도 값 정제
        mapx = cleanCoordinate(mapx);
        mapy = cleanCoordinate(mapy);

        // 유효성 검사 추가
        if (mapx.equals("0") || mapy.equals("0")) {
          log.warn("유효하지 않은 좌표값 (mapx: {}, mapy: {})", mapx, mapy);
          continue;
        }

        TourInfo tourInfo = TourInfo.builder()
            .areaCode(areaCode)
            .sigunguCode((String) itemObject.get("sigungucode"))
            .title((String) itemObject.get("title"))
            .addr1((String) itemObject.get("addr1"))
            .addr2((String) itemObject.get("addr2"))
            .contentTypeId((String) itemObject.get("contenttypeid"))
            .contentId((String) itemObject.get("contentid"))
            .mapx(mapx)
            .mapy(mapy)
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

  // 좌표 값 정제
  private String cleanCoordinate(String coordinate) {
    if (coordinate == null || coordinate.isEmpty()) {
      return "0";
    }

    String cleanedCoordinate = coordinate.replaceAll("[^0-9.-]", "");

    if (cleanedCoordinate.isEmpty()) {
      return "0";
    }

    // 유효성 검사 추가
    if (!cleanedCoordinate.matches("-?\\d+(\\.\\d+)?")) {
      return "0";
    }

    return cleanedCoordinate;
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
  public Page<TourInfoDocument> searchByTitle(String keyword, Pageable pageable) {
    return tourInfoDocumentRepository.findByTitleContaining(keyword, pageable);
  }

  // 주소로 검색
  public Page<TourInfoDocument> searchByAddress(String addr, Pageable pageable) {
    return tourInfoDocumentRepository.findByAddr1ContainingOrAddr2Containing(addr, addr, pageable);
  }

  // 위치 기반으로 검색
  public Page<TourInfoDocument> searchByLocation(double lat, double lon, double distance, Pageable pageable) {
    Criteria criteria = new Criteria("location")
        .within(new GeoPoint(lat, lon), distance + "km");

    Query query = new CriteriaQuery(criteria).setPageable(pageable);

    SearchHits<TourInfoDocument> searchHits = elasticsearchOperations.search(query, TourInfoDocument.class);

    List<TourInfoDocument> tourInfoDocuments = searchHits.getSearchHits().stream()
        .map(SearchHit::getContent)
        .collect(Collectors.toList());

    return new PageImpl<>(tourInfoDocuments, pageable, searchHits.getTotalHits());
  }

  // 주소를 좌표로 변환
  public GeoPoint convertToLocation(String address) {
    try {
      // Geocoding API URL 생성
      String apiUrl = geocodingUrl + "?query=" + URLEncoder.encode(address, StandardCharsets.UTF_8);

      URL url = new URL(apiUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      // API 인증을 위한 헤더 설정
      connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
      connection.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

      // 응답 코드 확인
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        // JSON 파싱
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response.toString());
        JSONArray addresses = (JSONArray) jsonObject.get("addresses");

        if (addresses != null && !addresses.isEmpty()) {
          JSONObject result = (JSONObject) addresses.get(0);
          double lat = Double.parseDouble(result.get("y").toString());
          double lon = Double.parseDouble(result.get("x").toString());

          return new GeoPoint(lat, lon);
        } else {
          log.warn("주소 좌표 변환 실패: 응답에 주소 정보가 없습니다. 응답: {}", response);
        }
      } else {
        log.error("주소 좌표 변환 실패: API 응답 코드 {}", responseCode);
      }
    } catch (Exception e) {
      log.error("주소를 좌표로 변환 실패", e);
    }

    return null;
  }

}