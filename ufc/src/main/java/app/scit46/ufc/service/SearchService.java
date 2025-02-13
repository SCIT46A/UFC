package app.scit46.ufc.service;

import app.scit46.ufc.dto.CampaignDTO;
import app.scit46.ufc.dto.SearchDTO;
import app.scit46.ufc.entity.CampaignEntity;
import app.scit46.ufc.entity.SearchEntity;
import app.scit46.ufc.repository.SearchRepository;
import app.scit46.ufc.util.HangulUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    public List<SearchDTO> search_target(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // (1) 띄어쓰기 제거된 검색어
        String normalizedQuery = keyword.replaceAll("\\s+", "");

        // (2) DB에서 공백 제거 제목 기준 LIKE 검색 (Campaign + Product)
        List<SearchEntity> candidateA = searchRepository.searchByKeyword(normalizedQuery);

        // (3) 초성 검색을 위한 검색어의 초성 추출
        String queryInitials = HangulUtils.extractInitialConsonants(normalizedQuery);

        // (4) 전체 데이터에서 초성 검색 수행 (Campaign + Product)
        List<SearchEntity> allEntities = searchRepository.findAll();

        // 결과를 담을 Set (중복 제거)
        Set<SearchEntity> results = new HashSet<>(candidateA);

        for (SearchEntity entity : allEntities) {
            // 제목에서 띄어쓰기 제거
            String normalizedTitle = entity.getName().replaceAll("\\s+", "");
            // 초성 추출 (예: "간다" → "ㄱㄷ")
            String titleInitials = HangulUtils.extractInitialConsonants(normalizedTitle);
            if (titleInitials.contains(queryInitials)) {
                results.add(entity);
            }
        }

        return results.stream()
                .map(SearchDTO::toEntity)
                .collect(Collectors.toList());
    }




}