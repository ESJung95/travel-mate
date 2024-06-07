package com.eunsun.travel_mate.service.tourinfo;

import com.eunsun.travel_mate.domain.AreaCode;
import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import com.eunsun.travel_mate.domain.tourInfo.TourInfoDocument;
import com.eunsun.travel_mate.repository.elasticsearch.TourInfoSearchRepository;
import com.eunsun.travel_mate.repository.jpa.AreaCodeRepository;
import com.eunsun.travel_mate.repository.jpa.TourInfoRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourInfoService {

  public static String apiKeyTest;

  @Value("${tour.openapi.key}")
  private String apiKey;

  private final TourInfoRepository tourInfoRepository;

  private final AreaCodeRepository areaCodeRepository;

  private final TourInfoSearchRepository tourInfoSearchRepository;

  // 여행지명을 검색
  public List<TourInfoDocument> searchByTitle(String keyword) {
    return tourInfoSearchRepository.findByTitleContaining(keyword);
  }

  // 주소로 검색
  public List<TourInfoDocument> searchByAddress(String addr) {
    return tourInfoSearchRepository.findByAddr1ContainingOrAddr2Containing(addr, addr);
  }

  // 매달 지역별 여행 정보 확인
  @Transactional
  @Scheduled(cron = "0 0 0 1 * *") // 매월 1일에 실행
  public void checkAndUpdateTourInfos() {
    log.info("OpenApi 지역별 여행 정보 변경 사항 확인 시작");

    // 새로운 전체 여행 정보 데이터 가져오기
    List<TourInfo> newTourInfos = new ArrayList<>();
    try {
      getTourInfoFromApi();
    } catch (ParseException e) {
      log.error("여행 정보 데이터 파싱 실패", e);
      return;
    }

    // 기존 데이터와 비교하여 변경 사항 확인
    List<TourInfo> existingTourInfos = tourInfoRepository.findAll();

    if (!newTourInfos.equals(existingTourInfos)) {
      log.info("여행 정보 변동 사항 존재");

      tourInfoRepository.deleteAll();
      tourInfoRepository.saveAll(newTourInfos);
      log.info("여행 정보 업데이트 성공");

    } else {
      log.info("여행 정보 변동 사항 없음");
    }
  }

  // 지역별 여행 정보 가져와서 저장하기
  public void getTourInfoFromApi() throws ParseException {
    List<AreaCode> areaCodes = areaCodeRepository.findAll();
    List<TourInfo> allTourInfos = new ArrayList<>(); // 전체 TourInfo 객체를 저장할 리스트

    // 지역별 여행 정보 데이터 가져오기 -> areaCode 전부
    for (AreaCode areaCode : areaCodes) {
      List<TourInfo> tourInfos = getTourInfoByAreaCode(areaCode);
      allTourInfos.addAll(tourInfos);
    }

    // 모든 파싱한 데이터 Entity 저장하기
    tourInfoRepository.saveAll(allTourInfos);

  }


  // OpenApi 에서 지역 여행 정보 데이터 가져오기
  public String getTourInfoString(String areaCode, int pageNo, int numOfRows) {
    // pageNo : 페이지번호 , numOfRows : 한페이지결과수
    String apiUrl =
        "https://apis.data.go.kr/B551011/KorService1/areaBasedList1?MobileOS=ETC&MobileApp=TEST&_type=json&areaCode="
            + areaCode + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows + "&serviceKey=" + apiKey;
    HttpURLConnection connection = null;
    try {
      URL url = new URL(apiUrl);
      connection = (HttpURLConnection) url.openConnection();
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

      log.info("JSON Response: {}", response);
      br.close();

      return response.toString();

    } catch (Exception e) {
      log.error("지역별 여행 정보 가져오기 실패", e);
      return "지역별 여행 정보 가져오기 실패";

    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  // 데이터 파싱하기
  public List<TourInfo> parseTourInfo(String jsonString, AreaCode areaCode) {
    List<TourInfo> tourInfos = new ArrayList<>();

    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject;

    try {
      jsonObject = (JSONObject) jsonParser.parse(jsonString);

      JSONObject response = (JSONObject) jsonObject.get("response");
      JSONObject body = (JSONObject) response.get("body");
      JSONObject items = (JSONObject) body.get("items");
      JSONArray itemArray = (JSONArray) items.get("item");

      for (Object item : itemArray) {
        JSONObject itemObject = (JSONObject) item;

        TourInfo tourInfo = new TourInfo();
        tourInfo.setAreaCode(areaCode);
        tourInfo.setAddr1((String) itemObject.get("addr1"));
        tourInfo.setAddr2((String) itemObject.get("addr2"));
        tourInfo.setContentId((String) itemObject.get("contentid"));
        tourInfo.setContentTypeId((String) itemObject.get("contenttypeid"));
        tourInfo.setCreatedTime((String) itemObject.get("createdtime"));
        tourInfo.setModifiedTime((String) itemObject.get("modifiedtime"));
        tourInfo.setImageUrl1((String) itemObject.get("firstimage"));
        tourInfo.setImageUrl2((String) itemObject.get("firstimage2"));
        tourInfo.setMapx((String) itemObject.get("mapx"));
        tourInfo.setMapy((String) itemObject.get("mapy"));
        tourInfo.setSigunguCode((String) itemObject.get("sigungucode"));
        tourInfo.setTitle((String) itemObject.get("title"));
        tourInfos.add(tourInfo);

      }

    } catch(ParseException e){
      throw new RuntimeException("TourInfo 데이터 파싱 실패", e);
    }

    return tourInfos;
  }

  // totalCount 확인하고 나눠서 데이터 가져오기
  public List<TourInfo> getTourInfoByAreaCode(AreaCode areaCode) throws ParseException {
    List<TourInfo> tourInfos = new ArrayList<>();
    int pageNo = 1;
    int numOfRows = 10;
    int totalCount;

    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject;

    do {
      String tourInfoData = getTourInfoString(areaCode.getCode(), pageNo, numOfRows);
      jsonObject = (JSONObject) jsonParser.parse(tourInfoData);


      JSONObject response = (JSONObject) jsonObject.get("response");
      JSONObject body = (JSONObject) response.get("body");
      totalCount = Integer.parseInt(body.get("totalCount").toString());

      // 데이터 파싱하기
      List<TourInfo> pageTourInfos = parseTourInfo(tourInfoData, areaCode);
      tourInfos.addAll(pageTourInfos);

      pageNo++;

    } while (pageNo <= Math.ceil(totalCount / 10.0));

    return tourInfos;
  }

}

