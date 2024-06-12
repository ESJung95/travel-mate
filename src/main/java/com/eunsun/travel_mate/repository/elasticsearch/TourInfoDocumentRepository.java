package com.eunsun.travel_mate.repository.elasticsearch;

import com.eunsun.travel_mate.domain.tourInfo.TourInfoDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourInfoDocumentRepository extends ElasticsearchRepository<TourInfoDocument, Long> {

  // 여행지명으로 검색
  Page<TourInfoDocument> findByTitleContaining(String keyword, Pageable pageable);

  // 주소로 검색 (addr1 또는 addr2에 포함)
  Page<TourInfoDocument> findByAddr1ContainingOrAddr2Containing(String addr1, String addr2, Pageable pageable);

}