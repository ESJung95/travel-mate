package com.eunsun.travel_mate.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eunsun.travel_mate.domain.AreaCode;
import com.eunsun.travel_mate.domain.TourInfo;
import com.eunsun.travel_mate.repository.AreaCodeRepository;
import com.eunsun.travel_mate.repository.TourInfoRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TourInfoServiceTest {

  @Mock
  private TourInfoRepository tourInfoRepository;

  @Mock
  private AreaCodeRepository areaCodeRepository;

  @Spy
  @InjectMocks
  private TourInfoService tourInfoService;

  @BeforeEach
  void setUp() {
    // TourInfoService apiKey 설정
    TourInfoService.apiKey = "testApiKey";
  }

  @Test
  @DisplayName("지역별 여행 정보 데이터 업데이트 테스트")
  void checkAndUpdateTourInfos() throws Exception {
    // given
    List<TourInfo> existingTourInfos = new ArrayList<>();

    TourInfo tourInfo1 = new TourInfo();
    tourInfo1.setContentId("1");
    tourInfo1.setTitle("Tour 1");

    TourInfo tourInfo2 = new TourInfo();
    tourInfo2.setContentId("2");
    tourInfo2.setTitle("Tour 2");

    existingTourInfos.add(tourInfo1);
    existingTourInfos.add(tourInfo2);

    List<TourInfo> newTourInfos = new ArrayList<>(existingTourInfos);

    TourInfo tourInfo3 = new TourInfo();
    tourInfo3.setContentId("3");
    tourInfo3.setTitle("Tour 3");
    newTourInfos.add(tourInfo3);

    when(tourInfoRepository.findAll()).thenReturn(existingTourInfos);

    doReturn(newTourInfos).when(tourInfoService).getTourInfoByAreaCode(any(AreaCode.class));
    when(areaCodeRepository.findAll()).thenReturn(Collections.singletonList(new AreaCode()));

    // when
    tourInfoService.checkAndUpdateTourInfos();

    // then
    verify(tourInfoRepository, times(1)).deleteAll();
    verify(tourInfoRepository, times(1)).saveAll(newTourInfos);
  }

  @Test
  @DisplayName("OpenApi 에서 지역별 여행 정보 데이터 가져오기 테스트")
  void getTourInfoFromApi() throws ParseException {
    // given
    List<AreaCode> areaCodes = new ArrayList<>();
    AreaCode areaCode1 = new AreaCode();
    areaCode1.setCode("1");
    areaCode1.setName("서울");

    AreaCode areaCode2 = new AreaCode();
    areaCode2.setCode("2");
    areaCode2.setName("부산");

    areaCodes.add(areaCode1);
    areaCodes.add(areaCode2);

    List<TourInfo> tourInfosSeoul = new ArrayList<>();
    TourInfo tourInfo1 = new TourInfo();
    tourInfo1.setContentId("1");
    tourInfo1.setTitle("Tour 1");
    tourInfo1.setAreaCode(areaCode1);

    tourInfosSeoul.add(tourInfo1);

    List<TourInfo> tourInfosBusan = new ArrayList<>();
    TourInfo tourInfo2 = new TourInfo();
    tourInfo2.setContentId("2");
    tourInfo2.setTitle("Tour 2");
    tourInfo2.setAreaCode(areaCode2);

    tourInfosBusan.add(tourInfo2);

    List<TourInfo> allTourInfos = new ArrayList<>();
    allTourInfos.addAll(tourInfosSeoul);
    allTourInfos.addAll(tourInfosBusan);

    when(areaCodeRepository.findAll()).thenReturn(areaCodes);
    doReturn(tourInfosSeoul).when(tourInfoService).getTourInfoByAreaCode(areaCode1);
    doReturn(tourInfosBusan).when(tourInfoService).getTourInfoByAreaCode(areaCode2);

    // when
    tourInfoService.getTourInfoFromApi();

    // then
    verify(tourInfoRepository, times(1)).saveAll(allTourInfos);
  }
}
