package com.eunsun.travel_mate.service;

import com.eunsun.travel_mate.domain.AreaCode;
import com.eunsun.travel_mate.repository.AreaCodeRepository;
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
public class AreaCodeService {

  @Value("${tour.openapi.key}")
  String apiKey;

  private final AreaCodeRepository areaCodeRepository;


  // 매달 지역코드 확인
  @Transactional
  @Scheduled(cron = "0 0 0 1 * *") // 매월 1일에 실행
  public void checkAndUpdateAreaCodes() {
    log.info("OpenApi 지역코드 변경 사항 확인 시작");

    // 지역 코드 데이터 가져오기
    String areaCodeData = getAreaCodeString();

    // 데이터 파싱하기
    List<AreaCode> newAreaCodes = parseAreaCode(areaCodeData);

    // 기존 데이터와 비교하여 변경 사항 확인
    List<AreaCode> existingAreaCodes = areaCodeRepository.findAll();

    if (!newAreaCodes.equals(existingAreaCodes)) {
      log.info("지역 코드 변동 사항 존재");

      areaCodeRepository.deleteAll();
      areaCodeRepository.saveAll(newAreaCodes);
      log.info("지역 코드 업데이트 성공");

    } else {
      log.info("지역 코드 변동 사항 없음");
    }
  }

  // 지역코드 가져와서 저장하기
  public List<AreaCode> getAreaCodeFromApi() {
    // 지역 코드 데이터 가져오기
    String areaCodeData = getAreaCodeString();

    // 데이터 파싱하기
    List<AreaCode> areaCodes = parseAreaCode(areaCodeData);

    // 파싱한 데이터 Entity 저장
    areaCodeRepository.saveAll(areaCodes);

    return areaCodes;
  }

  // OpenApi 에서 areaCode 데이터 가져오기
  public String getAreaCodeString() {
    String apiUrl =
        "https://apis.data.go.kr/B551011/KorService1/areaCode1?numOfRows=17&MobileOS=ETC&MobileApp=TEST&_type=json&serviceKey="
            + apiKey;

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
      log.error("지역 코드 가져오기 실패", e);
      return "AreaCode 가져오기 실패";
    }
  }

    // 데이터 파싱하기
    public List<AreaCode> parseAreaCode(String jsonString) {
      List<AreaCode> areaCodes = new ArrayList<>();

      JSONParser jsonParser = new JSONParser();

      try {
        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);

        JSONObject response = (JSONObject) jsonObject.get("response");
        JSONObject body = (JSONObject) response.get("body");
        JSONObject items = (JSONObject) body.get("items");
        JSONArray itemArray = (JSONArray) items.get("item");

        for (Object item : itemArray) {
          JSONObject itemObject = (JSONObject) item;
          String code = (String) itemObject.get("code");
          String name = (String) itemObject.get("name");

          AreaCode areaCode = new AreaCode();
          areaCode.setCode(code);
          areaCode.setName(name);

          areaCodes.add(areaCode);
        }

      } catch (ParseException e) {
        throw new RuntimeException("AreaCode 데이터 파싱 실패", e);
      }

      return areaCodes;
    }


}
