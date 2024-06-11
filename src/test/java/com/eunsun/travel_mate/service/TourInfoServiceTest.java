package com.eunsun.travel_mate.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eunsun.travel_mate.domain.tourInfo.TourInfoDocument;
import com.eunsun.travel_mate.repository.elasticsearch.TourInfoDocumentRepository;
import com.eunsun.travel_mate.repository.jpa.AreaCodeRepository;
import com.eunsun.travel_mate.repository.jpa.TourInfoRepository;
import com.eunsun.travel_mate.service.tourinfo.TourInfoService;
import java.util.ArrayList;
import java.util.List;
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
    // TourInfoService apiKey 설정
    TourInfoService.apiKeyTest = "testApiKey";
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
}