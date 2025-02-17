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

    // ✅ 모든 태그를 포함하는지 확인하는 메서드
    private boolean containsAllTags(List<String> dtoTags, List<String> searchTags) {
        return searchTags.stream().allMatch(dtoTags::contains);
    }

    @Transactional
    public List<SearchResultDTO> search(String keyword, String sortType, String donationFilter, List<String> tagFilters) {
        List<SearchResultDTO> results = new ArrayList<>();

        // ✅ 전체 데이터를 가져오도록 변경
        List<CampaignEntity> campaigns = keyword.isEmpty() ? campaignRepository.findAll() : campaignRepository.findByTitleContaining(keyword);
        for (CampaignEntity campaign : campaigns) {
            Long imageId = (campaign.getPhoto() != null) ? campaign.getPhoto().getPhotoId() : null;
            String sellerName = (campaign.getCreatedBy() != null) ? campaign.getCreatedBy().getBName() : "Unknown";
            String description = campaign.getBoards().isEmpty() ? "" : campaign.getBoards().get(0).getTitle();
            LocalDateTime now = LocalDateTime.now();
            long remainingDays = ChronoUnit.DAYS.between(now, campaign.getEndDate());

            int totalDonated = campaign.getMaterialDonations().stream().mapToInt(d -> d.getQuantity()).sum();
            int totalRequired = campaign.getCampaignGoals().stream().mapToInt(g -> g.getQuantityRequired()).sum();
            double donationPercentage = totalRequired > 0 ? ((double) totalDonated / totalRequired) * 100 : 0;

            int likes = likeRepository.countByCampaign(campaign);
            List<String> tags = campaign.getCampaignTags().stream().map(ct -> ct.getTag().getContent()).collect(Collectors.toList());

            SearchResultDTO dto = SearchResultDTO.builder()
                    .originalId(campaign.getCampaignId())
                    .type("campaign")
                    .imageId(imageId)
                    .sellerName(sellerName)
                    .title(campaign.getTitle())
                    .description(description)
                    .price(null)
                    .remainingDays(remainingDays)
                    .donatedQuantity(totalDonated)
                    .donationPercentage(donationPercentage)
                    .createdDate(campaign.getCreatedDate())
                    .likes(likes)
                    .tags(tags)
                    .build();
            results.add(dto);
        }

        // ✅ 전체 데이터를 가져오도록 변경
        List<ProductEntity> products = keyword.isEmpty() ? productRepository.findAll() : productRepository.findByItem_NameContaining(keyword);
        for (ProductEntity product : products) {
            Long imageId = (product.getItem().getPhoto() != null) ? product.getItem().getPhoto().getPhotoId() : null;
            String sellerName = (product.getCreatedBy() != null) ? product.getCreatedBy().getBName() : "Unknown";
            String title = product.getItem().getName();
            String description = product.getItem().getDescription();
            Integer price = product.getItem().getPrice();
            int likes = likeRepository.countByProduct(product);
            List<String> tags = product.getProductTags().stream().map(pt -> pt.getTag().getContent()).collect(Collectors.toList());

            SearchResultDTO dto = SearchResultDTO.builder()
                    .originalId(product.getProductId())
                    .type("product")
                    .imageId(imageId)
                    .sellerName(sellerName)
                    .title(title)
                    .description(description)
                    .price(price)
                    .remainingDays(null)
                    .donatedQuantity(null)
                    .donationPercentage(null)
                    .createdDate(null)
                    .likes(likes)
                    .tags(tags)
                    .build();
            results.add(dto);
        }

        // ✅ 태그 필터 적용 (모든 태그를 포함하는 항목만 남김)
        if (tagFilters != null && !tagFilters.isEmpty()) {
            results = results.stream()
                    .filter(dto -> containsAllTags(dto.getTags(), tagFilters))
                    .collect(Collectors.toList());
        }

        // ✅ 기부 퍼센트 필터링 추가
        if (donationFilter != null) {
            switch (donationFilter) {
                case "below50":  // 50% 이하
                    results = results.stream()
                            .filter(dto -> dto.getDonationPercentage() != null && dto.getDonationPercentage() <= 50)
                            .collect(Collectors.toList());
                    break;
                case "between51to100":  // 51~100%
                    results = results.stream()
                            .filter(dto -> dto.getDonationPercentage() != null && dto.getDonationPercentage() > 50 && dto.getDonationPercentage() < 100)
                            .collect(Collectors.toList());
                    break;
                case "above100":  // 100% 이상
                    results = results.stream()
                            .filter(dto -> dto.getDonationPercentage() != null && dto.getDonationPercentage() >= 100)
                            .collect(Collectors.toList());
                    break;
            }
        }

        // ✅ 정렬 적용
        if (sortType == null || sortType.equals("null")) {
            results.sort(Comparator.comparingInt(dto -> SimilarityUtil.computeDistance(dto.getTitle(), keyword)));
        } else {
            switch (sortType) {
                case "latest":
                    results.sort(Comparator.comparing(SearchResultDTO::getCreatedDate, Comparator.nullsLast(Comparator.reverseOrder())));
                    break;
                case "deadline":
                    results = results.stream().filter(dto -> dto.getRemainingDays() != null).collect(Collectors.toList());
                    results.sort(Comparator.comparing(SearchResultDTO::getRemainingDays, Comparator.nullsLast(Comparator.naturalOrder())));
                    break;
                case "like":
                    results.sort(Comparator.comparing((SearchResultDTO dto) -> dto.getLikes() == null ? 0 : dto.getLikes()).reversed());
                    break;
                default:
                    results.sort(Comparator.comparingInt(dto -> SimilarityUtil.computeDistance(dto.getTitle(), keyword)));
            }
        }

        return results;
    }



}
