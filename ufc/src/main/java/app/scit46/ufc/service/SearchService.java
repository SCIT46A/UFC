package app.scit46.ufc.service;

import java.util.*;
import java.util.stream.Collectors;

import app.scit46.ufc.dto.custom.CampaignWithGoalsDTO;
import app.scit46.ufc.dto.custom.IntroPageCampaignDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.SearchDTO;
import app.scit46.ufc.dto.SearchResultDTO;
import app.scit46.ufc.dto.TagDTO;
import app.scit46.ufc.entity.SearchEntity;
import app.scit46.ufc.entity.TagEntity;
import app.scit46.ufc.repository.LikeRepository;
import app.scit46.ufc.repository.ProductRepository;
import app.scit46.ufc.repository.SearchRepository;
import app.scit46.ufc.repository.campaign.CampaignRepository;
import app.scit46.ufc.repository.tag.TagRepository;
import app.scit46.ufc.util.HangulUtils;
import app.scit46.ufc.util.SimilarityUtil;

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
                                           List<String> tagFilters,
                                           Long userLoginId) {
        List<SearchResultDTO> results = searchRepository.findSearchResults(keyword, userLoginId);
        results.sort(Comparator.comparingInt(dto -> SimilarityUtil.computeDistance(dto.getTitle(), keyword)));
        return applyFiltersAndSorting(results, sortType, donationFilter, tagFilters);
    }

    @Transactional(readOnly = true)
    public List<SearchResultDTO> getOngoingCampaigns(String sortType,
                                                     String donationFilter,
                                                     List<String> tagFilters,
                                                     Long userLoginId) {
        List<SearchResultDTO> results = searchRepository.findOngoingCampaigns(userLoginId);
        return applyFiltersAndSorting(results, sortType, donationFilter, tagFilters);
    }

    @Transactional(readOnly = true)
    public List<SearchResultDTO> getUpcomingCampaigns(String sortType,
                                                      String donationFilter,
                                                      List<String> tagFilters,
                                                      Long userLoginId) {
        List<SearchResultDTO> results = searchRepository.findUpcomingCampaigns(userLoginId);
        return applyFiltersAndSorting(results, sortType, donationFilter, tagFilters);
    }

    @Transactional(readOnly = true)
    public List<SearchResultDTO> getSales(String sortType, List<String> tagFilters, Long userLoginId) {
        List<SearchResultDTO> results = searchRepository.findSales(userLoginId);
        return applyFiltersAndSorting(results, sortType, null, tagFilters);
    }

    @Transactional(readOnly = true)
    public List<SearchResultDTO> findTop10CampaignsByLikes(Long userLoginId){
        return searchRepository.findTop10CampaignsByLikes(userLoginId);
    }

    @Transactional(readOnly = true)
    public List<SearchResultDTO> findTop10ProductsByLikes(Long userLoginId){
        return searchRepository.findTop10ProductsByLikes(userLoginId);
    }

    @Transactional(readOnly = true)
    public List<CampaignWithGoalsDTO> getOngoingCampaignsWithGoals() {
        // 평탄한 결과(캠페인×목표 행) 전체를 조회 (LIMIT 제거)
        List<IntroPageCampaignDTO> rows = campaignRepository.findCampaignGoalRows(); // 쿼리에서 LIMIT을 제거

        // 캠페인 ID 기준 그룹화
        Map<Long, List<IntroPageCampaignDTO>> grouped =
                rows.stream().collect(Collectors.groupingBy(IntroPageCampaignDTO::getCampaignId));

        // 그룹별로 최종 DTO로 변환
        List<CampaignWithGoalsDTO> allCampaigns = new ArrayList<>();
        for (Map.Entry<Long, List<IntroPageCampaignDTO>> entry : grouped.entrySet()) {
            List<IntroPageCampaignDTO> dtoList = entry.getValue();
            IntroPageCampaignDTO first = dtoList.get(0);

            List<CampaignWithGoalsDTO.GoalInfo> goals = dtoList.stream()
                    .map(row -> new CampaignWithGoalsDTO.GoalInfo(
                            row.getGoalId() == null ? 0L : row.getGoalId(),
                            row.getGoalTitle() == null ? "" : row.getGoalTitle(),
                            row.getRequiredQuantity() == null ? 0 : row.getRequiredQuantity(),
                            row.getDonatedQuantity() == null ? 0 : row.getDonatedQuantity(),
                            row.getDonationPercentage() == null ? 0.0 : row.getDonationPercentage(),
                            row.getTotalDonors() == null ? 0 : row.getTotalDonors()
                    ))
                    .collect(Collectors.toList());

            CampaignWithGoalsDTO campaignDto = new CampaignWithGoalsDTO();
            campaignDto.setCampaignId(first.getCampaignId());
            campaignDto.setType(first.getType());
            campaignDto.setImageId(first.getImageId());
            campaignDto.setSellerName(first.getSellerName());
            campaignDto.setCampaignTitle(first.getCampaignTitle());
            campaignDto.setCampaignDescription(first.getCampaignDescription());
            campaignDto.setGoals(goals);
            campaignDto.setCampaignDonors(first.getCampaignDonors() == null ? 0 : first.getCampaignDonors());

            allCampaigns.add(campaignDto);
        }

        // 최종 결과 캠페인이 3개만 필요하므로, 예를 들어 donationPercentage 기준 오름차순으로 정렬 후 상위 3개 선택
        List<CampaignWithGoalsDTO> result = allCampaigns.stream()
                .sorted((a, b) -> {
                    // 각 캠페인의 전체 donationPercentage 기준으로 비교(예시: 목표들 중 최소값, 또는 평균값 등 원하는 기준 사용)
                    // 여기서는 각 캠페인의 첫 번째 목표의 donationPercentage를 비교합니다.
                    double ap = a.getGoals().isEmpty() ? 0 : a.getGoals().get(0).getDonationPercentage();
                    double bp = b.getGoals().isEmpty() ? 0 : b.getGoals().get(0).getDonationPercentage();
                    return Double.compare(ap, bp);
                })
                .limit(3)
                .collect(Collectors.toList());

        return result;
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
