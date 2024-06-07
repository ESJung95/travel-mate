package com.eunsun.travel_mate.repository.elasticsearch;

import com.eunsun.travel_mate.domain.tourInfo.TourInfoDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourInfoSearchRepository extends ElasticsearchRepository<TourInfoDocument, String> {

  // 여행지명으로 검색
  List<TourInfoDocument> findByTitleContaining(String keyword);

  // 주소로 검색 (addr1 또는 addr2에 포함)
  List<TourInfoDocument> findByAddr1ContainingOrAddr2Containing(String addr1, String addr2);

  // 지역 코드로 검색
  List<TourInfoDocument> findByAreaCode(String areaCode);

  // 시군구 코드로 검색
  List<TourInfoDocument> findBySigunguCode(String sigunguCode);

  // 콘텐츠 타입 ID로 검색
  List<TourInfoDocument> findByContentTypeId(String contentTypeId);

  // 제목과 주소로 검색
  List<TourInfoDocument> findByTitleContainingAndAddr1ContainingOrAddr2Containing(String title, String addr1, String addr2);

}