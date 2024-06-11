package com.eunsun.travel_mate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eunsun.travel_mate.domain.AreaCode;
import com.eunsun.travel_mate.repository.jpa.AreaCodeRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AreaCodeServiceTest {

  @Mock
  private AreaCodeRepository areaCodeRepository;

  @Spy
  @InjectMocks
  private AreaCodeService areaCodeService;

  @BeforeEach
  void setUp() {
    // AreaCodeService apiKey 설정
    ReflectionTestUtils.setField(areaCodeService, "apiKey", "testApiKey");
  }

  @Test
  @DisplayName("지역 코드 데이터 업데이트 기능 테스트")
  void checkAndUpdateAreaCodes() {
    // given
    List<AreaCode> existingAreaCodes = new ArrayList<>();

    AreaCode areaCode1 = new AreaCode();
    areaCode1.setCode("1");
    areaCode1.setName("서울");

    AreaCode areaCode2 = new AreaCode();
    areaCode2.setCode("2");
    areaCode2.setName("경기");

    existingAreaCodes.add(areaCode1);
    existingAreaCodes.add(areaCode2);

    List<AreaCode> newAreaCodes = new ArrayList<>();
    AreaCode newAreaCode1 = new AreaCode();
    newAreaCode1.setCode("1");
    newAreaCode1.setName("서울");

    AreaCode newAreaCode2 = new AreaCode();
    newAreaCode2.setCode("2");
    newAreaCode2.setName("경기");

    AreaCode newAreaCode3 = new AreaCode();
    newAreaCode3.setCode("3");
    newAreaCode3.setName("부산");

    newAreaCodes.add(newAreaCode1);
    newAreaCodes.add(newAreaCode2);
    newAreaCodes.add(newAreaCode3);

    when(areaCodeRepository.findAll()).thenReturn(existingAreaCodes);
    doReturn("mockedResponse").when(areaCodeService).getAreaCodeString();
    doReturn(newAreaCodes).when(areaCodeService).parseAreaCode(anyString());

    // when
    areaCodeService.checkAndUpdateAreaCodes();

    // then
    verify(areaCodeRepository, times(1)).deleteAll();
    verify(areaCodeRepository, times(1)).saveAll(newAreaCodes);
  }

  @Test
  @DisplayName("지역 코드 변동 사항이 없는 경우")
  void checkAndUpdateAreaCodes_NoChanges() {
    // given
    List<AreaCode> existingAreaCodes = new ArrayList<>();
    AreaCode areaCode1 = new AreaCode();
    areaCode1.setCode("1");
    areaCode1.setName("서울");

    AreaCode areaCode2 = new AreaCode();
    areaCode2.setCode("2");
    areaCode2.setName("경기");

    existingAreaCodes.add(areaCode1);
    existingAreaCodes.add(areaCode2);

    List<AreaCode> newAreaCodes = new ArrayList<>();
    newAreaCodes.add(areaCode1);
    newAreaCodes.add(areaCode2);

    when(areaCodeRepository.findAll()).thenReturn(existingAreaCodes);
    doReturn("mockedResponse").when(areaCodeService).getAreaCodeString();
    doReturn(newAreaCodes).when(areaCodeService).parseAreaCode(anyString());

    // when
    areaCodeService.checkAndUpdateAreaCodes();

    // then
    verify(areaCodeRepository, times(0)).deleteAll();
    verify(areaCodeRepository, times(0)).saveAll(newAreaCodes);
  }

  @Test
  @DisplayName("OpenApi 에서 지역 코드 데이터 가져오기 테스트")
  void getAreaCodeFromApi() {
    // given
    List<AreaCode> expectedAreaCodes = new ArrayList<>();

    AreaCode areaCode1 = new AreaCode();
    areaCode1.setCode("1");
    areaCode1.setName("서울");

    AreaCode areaCode2 = new AreaCode();
    areaCode2.setCode("2");
    areaCode2.setName("경기");

    expectedAreaCodes.add(areaCode1);
    expectedAreaCodes.add(areaCode2);

    doReturn("mockedResponse").when(areaCodeService).getAreaCodeString();
    doReturn(expectedAreaCodes).when(areaCodeService).parseAreaCode(anyString());

    // when
    List<AreaCode> actualAreaCodes = areaCodeService.getAreaCodeFromApi();

    // then
    assertEquals(expectedAreaCodes, actualAreaCodes);
    verify(areaCodeRepository, times(1)).saveAll(expectedAreaCodes);
  }

  @Test
  @DisplayName("지역 코드 API 호출 실패 테스트")
  void getAreaCodeString_ApiFailure() {
    // given
    doReturn("AreaCode 가져오기 실패").when(areaCodeService).getAreaCodeString();

    // when
    String response = areaCodeService.getAreaCodeString();

    // then
    assertEquals("AreaCode 가져오기 실패", response);
  }

  @Test
  @DisplayName("지역 코드 API 빈 응답 테스트")
  void parseAreaCode_EmptyResponse() {
    // given
    String emptyJsonResponse = "{\"response\":{\"body\":{\"items\":{\"item\":[]}}}}";

    // when
    List<AreaCode> areaCodes = areaCodeService.parseAreaCode(emptyJsonResponse);

    // then
    assertEquals(Collections.emptyList(), areaCodes);
  }
}
