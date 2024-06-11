package com.eunsun.travel_mate.repository.elasticsearch;

import com.eunsun.travel_mate.domain.tourInfo.TourInfoDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourInfoDocumentRepository extends ElasticsearchRepository<TourInfoDocument, Long> {
  // 여행지명으로 검색
  List<TourInfoDocument> findByTitleContaining(String keyword);

  // 주소로 검색 (addr1 또는 addr2에 포함)
  List<TourInfoDocument> findByAddr1ContainingOrAddr2Containing(String addr1, String addr2);

}