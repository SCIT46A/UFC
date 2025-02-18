package app.scit46.ufc.service;

import app.scit46.ufc.dto.CampaignDTO;
import app.scit46.ufc.dto.SearchDTO;
import app.scit46.ufc.dto.SearchResultDTO;
import app.scit46.ufc.dto.TagDTO;
import app.scit46.ufc.entity.CampaignEntity;
import app.scit46.ufc.entity.ProductEntity;
import app.scit46.ufc.entity.SearchEntity;
import app.scit46.ufc.entity.TagEntity;
import app.scit46.ufc.repository.*;
import app.scit46.ufc.util.HangulUtils;
import app.scit46.ufc.util.SimilarityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LikeRepository likeRepository;

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

        // 병렬 스트림을 활용하여 성능 개선
        allEntities.parallelStream().forEach(entity -> {
            String normalizedTitle = entity.getName().replaceAll("\\s+", "");
            String titleInitials = HangulUtils.extractInitialConsonants(normalizedTitle);
            if (titleInitials.contains(queryInitials)) {
                synchronized (results) {
                    results.add(entity);
                }
            }
        });

        return results.stream()
                .map(SearchDTO::toEntity)
                .collect(Collectors.toList());
    }

    public List<TagDTO> searchTagTarget(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedQuery = keyword.replaceAll("\\s+", "");
        List<TagEntity> candidateA = tagRepository.tagByKeyword(normalizedQuery);
        String queryInitials = HangulUtils.extractInitialConsonants(normalizedQuery);
        List<TagEntity> allEntities = tagRepository.findAll();
        Set<TagEntity> results = new HashSet<>(candidateA);

        allEntities.parallelStream().forEach(entity -> {
            String normalizedTitle = entity.getContent().replaceAll("\\s+", "");
            String titleInitials = HangulUtils.extractInitialConsonants(normalizedTitle);
            if (titleInitials.contains(queryInitials)) {
                synchronized (results) {
                    results.add(entity);
                }
            }
        });

        return results.stream()
                .map(TagDTO::toDTO)
                .collect(Collectors.toList());
    }





//    ---------------------------------------------------------------------------------


    @Transactional(readOnly = true)
    public List<SearchResultDTO> searchAll(String keyword,
                                           String sortType,
                                           String donationFilter,
                                           List<String> tagFilters) {
        List<SearchResultDTO> results = searchRepository.findSearchResults(keyword);

        // 🔹 유사도 기반 정렬 추가 (Levenshtein Distance 사용)
        results.sort(Comparator.comparingInt(dto -> SimilarityUtil.computeDistance(dto.getTitle(), keyword)));

        return applyFiltersAndSorting(results, sortType, donationFilter, tagFilters);
    }


    /**
     * ② 진행 중인 캠페인 조회
     * → 전체 진행 중인 데이터를 불러온 후 태그, 기부 퍼센트, 정렬 필터를 적용
     */
    @Transactional(readOnly = true)
    public List<SearchResultDTO> getOngoingCampaigns(String sortType,
                                                     String donationFilter,
                                                     List<String> tagFilters) {
        List<SearchResultDTO> results = searchRepository.findOngoingCampaigns();
        return applyFiltersAndSorting(results, sortType, donationFilter, tagFilters);
    }

    /**
     * ③ 진행 예정인 캠페인 조회
     * → 전체 진행 예정 데이터를 불러온 후 태그, 기부 퍼센트, 정렬 필터를 적용
     */
    @Transactional(readOnly = true)
    public List<SearchResultDTO> getUpcomingCampaigns(String sortType,
                                                      String donationFilter,
                                                      List<String> tagFilters) {
        List<SearchResultDTO> results = searchRepository.findUpcomingCampaigns();
        return applyFiltersAndSorting(results, sortType, donationFilter, tagFilters);
    }

    /**
     * ④ 판매 (제품) 조회
     * → 전체 판매 데이터를 불러온 후 태그와 정렬 필터를 적용
     * (제품은 기부 퍼센트 필터 대상이 아님)
     */
    @Transactional(readOnly = true)
    public List<SearchResultDTO> getSales(String sortType,
                                          List<String> tagFilters) {
        List<SearchResultDTO> results = searchRepository.findSales();
        return applyFiltersAndSorting(results, sortType, null, tagFilters);
    }

//  5. 주목할 만한 캠페인 3개
    @Transactional(readOnly = true)
    public List<SearchResultDTO> findLowestDonationRateCampaigns(){
        return searchRepository.findLowestDonationRateCampaigns();
    }

    //  6. 인기있는 캠페인 10개
    @Transactional(readOnly = true)
    public List<SearchResultDTO> findTop10CampaignsByLikes(){
        return searchRepository.findTop10CampaignsByLikes();
    }

    //  7. 인기있는 상품 10개
    @Transactional(readOnly = true)
    public List<SearchResultDTO> findTop10ProductsByLikes(){
        return searchRepository.findTop10ProductsByLikes();
    }

















    /**
     * 공통 필터 및 정렬 적용 메서드
     * - tagFilters: DTO의 tags(콤마 구분 문자열)를 분리하여 포함 여부 확인
     * - donationFilter: 캠페인 타입에만 적용
     * - sortType: 최신(latest), 마감일(deadline), 좋아요(like) 등
     */
    private List<SearchResultDTO> applyFiltersAndSorting(List<SearchResultDTO> results,
                                                         String sortType,
                                                         String donationFilter,
                                                         List<String> tagFilters) {
        // 1. 태그 필터 적용 (이미 List<String> 타입)
        if (tagFilters != null && !tagFilters.isEmpty()) {
            results = results.stream()
                    .filter(dto -> {
                        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
                            return dto.getTags().containsAll(tagFilters);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        // 2. 기부 퍼센트 필터 (캠페인 타입에만 적용)
        if (donationFilter != null) {
            results = results.stream()
                    .filter(dto -> {
                        if ("campaign".equals(dto.getType()) && dto.getDonationPercentage() != null) {
                            double perc = dto.getDonationPercentage();
                            switch (donationFilter) {
                                case "below50": return perc <= 50;
                                case "between51to100": return perc > 50 && perc < 100;
                                case "above100": return perc >= 100;
                                default: return true;
                            }
                        }
                        // 제품은 기부 퍼센트 필터 적용 대상이 아님
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        // 3. 정렬 처리
        if (sortType != null) {
            switch (sortType) {
                case "latest":
                    results.sort(Comparator.comparing(SearchResultDTO::getCreatedDate,
                            Comparator.nullsLast(Comparator.reverseOrder())));
                    break;
                case "deadline":
                    results = results.stream()
                            .filter(dto -> dto.getRemainingDays() != null)
                            .collect(Collectors.toList());
                    results.sort(Comparator.comparing(SearchResultDTO::getRemainingDays,
                            Comparator.nullsLast(Comparator.naturalOrder())));
                    break;
                case "like":
                    results.sort(Comparator.comparing(
                                    (SearchResultDTO dto) -> dto.getLikes() == null ? 0 : dto.getLikes())
                            .reversed());
                    break;
                default:
                    break;
            }
        }
        return results;
    }




}
