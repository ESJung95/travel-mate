package com.eunsun.travel_mate.service.tourinfo;

import com.eunsun.travel_mate.domain.tourInfo.TourInfo;
import com.eunsun.travel_mate.domain.tourInfo.TourInfoDocument;
import com.eunsun.travel_mate.repository.elasticsearch.TourInfoSearchRepository;
import com.eunsun.travel_mate.repository.jpa.TourInfoRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TourInfoIndexService {

  private final TourInfoRepository tourInfoRepository;
  private final TourInfoSearchRepository tourInfoSearchRepository;

  // 데이터 색인하기
  public void indexTourInfo() {

    // 데이터베이스에서 TourInfo 데이터 가져오기
    List<TourInfo> tourInfoList = tourInfoRepository.findAll();

    // TourInfo -> TourInfoDocument 변환
    List<TourInfoDocument> tourInfoDocumentList = tourInfoList.stream()
        .map(this::convertToTourInfoDocument)
        .collect(Collectors.toList());

    // ElasticSearch 에 색인
    tourInfoSearchRepository.saveAll(tourInfoDocumentList);
  }

  // TourInfoDocument 로 바꿔주기
  private TourInfoDocument convertToTourInfoDocument(TourInfo tourInfo) {
    TourInfoDocument tourInfoDocument = new TourInfoDocument();

    tourInfoDocument.setId(tourInfo.getTourInfoId().toString());
    tourInfoDocument.setAreaCode(tourInfo.getAreaCode().getCode());
    tourInfoDocument.setSigunguCode(tourInfo.getSigunguCode());
    tourInfoDocument.setTitle(tourInfo.getTitle());
    tourInfoDocument.setAddr1(tourInfo.getAddr1());
    tourInfoDocument.setAddr2(tourInfo.getAddr2());
    tourInfoDocument.setContentTypeId(tourInfo.getContentTypeId());
    tourInfoDocument.setContentId(tourInfo.getContentId());
    tourInfoDocument.setMapx(Double.parseDouble(tourInfo.getMapx()));
    tourInfoDocument.setMapy(Double.parseDouble(tourInfo.getMapy()));
    tourInfoDocument.setImageUrl1(tourInfo.getImageUrl1());
    tourInfoDocument.setImageUrl2(tourInfo.getImageUrl2());
    tourInfoDocument.setCreatedTime(tourInfo.getCreatedTime());
    tourInfoDocument.setModifiedTime(tourInfo.getModifiedTime());
    return tourInfoDocument;
  }


}
