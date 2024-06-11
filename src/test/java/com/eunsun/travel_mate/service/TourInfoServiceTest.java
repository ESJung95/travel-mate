package com.eunsun.travel_mate.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eunsun.travel_mate.domain.AreaCode;
import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import com.eunsun.travel_mate.domain.tourInfo.TourInfoDocument;
import com.eunsun.travel_mate.repository.elasticsearch.TourInfoDocumentRepository;
import com.eunsun.travel_mate.repository.jpa.AreaCodeRepository;
import com.eunsun.travel_mate.repository.jpa.TourInfoRepository;
import com.eunsun.travel_mate.service.tourinfo.TourInfoService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TourInfoServiceTest {

  @Mock
  private TourInfoRepository tourInfoRepository;

  @Mock
  private AreaCodeRepository areaCodeRepository;

  @Mock
  private TourInfoDocumentRepository tourInfoDocumentRepository;

  @Mock
  private ElasticsearchOperations elasticsearchOperations;

  @Spy
  @InjectMocks
  private TourInfoService tourInfoService;

  @BeforeEach
  void setUp() {
    // AreaCodeService apiKey 설정
    ReflectionTestUtils.setField(tourInfoService, "apiKey", "testApiKey");
  }
  @Test
  @DisplayName("여행지명 검색 테스트")
  void searchByTitle() {
    // given
    List<TourInfoDocument> expectedDocuments = new ArrayList<>();
    TourInfoDocument doc1 = new TourInfoDocument();
    doc1.setTitle("Seoul Tower");
    expectedDocuments.add(doc1);

    TourInfoDocument doc2 = new TourInfoDocument();
    doc2.setTitle("Seoul Palace");
    expectedDocuments.add(doc2);

    when(tourInfoDocumentRepository.findByTitleContaining("Seoul")).thenReturn(expectedDocuments);

    // when
    List<TourInfoDocument> actualDocuments = tourInfoService.searchByTitle("Seoul");

    // then
    verify(tourInfoDocumentRepository, times(1)).findByTitleContaining("Seoul");
    Assertions.assertEquals(expectedDocuments, actualDocuments);
  }

  @Test
  @DisplayName("주소로 여행지 검색 테스트")
  void searchByAddress() {
    // given
    List<TourInfoDocument> expectedDocuments = new ArrayList<>();
    TourInfoDocument doc1 = new TourInfoDocument();
    doc1.setAddr1("123 Seoul Street");
    expectedDocuments.add(doc1);

    TourInfoDocument doc2 = new TourInfoDocument();
    doc2.setAddr2("456 Seoul Avenue");
    expectedDocuments.add(doc2);

    when(tourInfoDocumentRepository.findByAddr1ContainingOrAddr2Containing("Seoul", "Seoul")).thenReturn(expectedDocuments);

    // when
    List<TourInfoDocument> actualDocuments = tourInfoService.searchByAddress("Seoul");

    // then
    verify(tourInfoDocumentRepository, times(1)).findByAddr1ContainingOrAddr2Containing("Seoul", "Seoul");
    Assertions.assertEquals(expectedDocuments, actualDocuments);
  }

  @Test
  @DisplayName("지역별 여행 정보 API에서 가져와서 저장하기 테스트")
  void getTourInfoFromApi() {
    // given
    List<AreaCode> areaCodes = new ArrayList<>();
    AreaCode areaCode = new AreaCode();
    areaCode.setCode("1");
    areaCodes.add(areaCode);

    when(areaCodeRepository.findAll()).thenReturn(areaCodes);
    doReturn(100).when(tourInfoService).getTotalCount(anyString());
    doReturn("mockedResponse").when(tourInfoService).getTourInfoString(anyString(), anyInt(), anyInt());

    List<TourInfo> tourInfoList = new ArrayList<>();
    TourInfo tourInfo = new TourInfo();
    tourInfo.setTourInfoId(1L);
    tourInfo.setAreaCode(areaCode);
    tourInfo.setSigunguCode("123");
    tourInfo.setTitle("Test Title");
    tourInfo.setAddr1("Test Address 1");
    tourInfo.setAddr2("Test Address 2");
    tourInfo.setContentTypeId("12");
    tourInfo.setContentId("ABC123");
    tourInfo.setMapx("123.0");
    tourInfo.setMapy("12.0");
    tourInfo.setImageUrl1("http://example.com/image1.jpg");
    tourInfo.setImageUrl2("http://example.com/image2.jpg");
    tourInfo.setCreatedTime("2024-06-12 10:00:00");
    tourInfo.setModifiedTime("2024-06-12 10:00:00");
    tourInfoList.add(tourInfo);

    doReturn(tourInfoList).when(tourInfoService).parseTourInfo(anyString(), any(AreaCode.class));

    // when
    tourInfoService.getTourInfoFromApi();

    // then
    verify(tourInfoRepository, times(1)).saveAll(tourInfoList);
    verify(elasticsearchOperations, times(1)).bulkIndex(any(List.class), any(IndexCoordinates.class));
  }

  @Test
  @DisplayName("매월 여행 정보 업데이트 테스트")
  void updateTourInfoMonthly() {
    // given
    List<AreaCode> areaCodes = new ArrayList<>();
    AreaCode areaCode = new AreaCode();
    areaCode.setCode("1");
    areaCodes.add(areaCode);

    when(areaCodeRepository.findAll()).thenReturn(areaCodes);
    doReturn(100).when(tourInfoService).getTotalCount(anyString());
    doReturn("mockedResponse").when(tourInfoService).getTourInfoString(anyString(), anyInt(), anyInt());

    List<TourInfo> newTourInfoList = new ArrayList<>();
    TourInfo newTourInfo = new TourInfo();
    newTourInfo.setTourInfoId(1L);
    newTourInfo.setModifiedTime("2024-01-01T00:00:00");
    newTourInfo.setAreaCode(areaCode);
    newTourInfo.setTitle("New Title");
    newTourInfo.setAddr1("New Address 1");
    newTourInfo.setMapx("37.1234");
    newTourInfo.setMapy("127.1234");


    TourInfo existingTourInfo = new TourInfo();
    existingTourInfo.setTourInfoId(1L);
    existingTourInfo.setModifiedTime("2023-01-01T00:00:00");
    existingTourInfo.setAreaCode(areaCode);
    existingTourInfo.setTitle("Existing Title");
    existingTourInfo.setAddr1("Existing Address 1");
    existingTourInfo.setMapx("37.5678");
    existingTourInfo.setMapy("127.5678");


    newTourInfoList.add(newTourInfo);

    doReturn(newTourInfoList).when(tourInfoService).parseTourInfo(anyString(), any(AreaCode.class));
    when(tourInfoRepository.findById(anyLong())).thenReturn(Optional.of(existingTourInfo));

    // when
    tourInfoService.updateTourInfoMonthly();

    // then
    verify(tourInfoRepository, times(1)).findById(anyLong());
    verify(tourInfoRepository, times(1)).save(any(TourInfo.class));
    verify(elasticsearchOperations, times(1)).index(any(IndexQuery.class), any());
  }
}