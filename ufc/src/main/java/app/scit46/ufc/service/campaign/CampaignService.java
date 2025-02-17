package app.scit46.ufc.service.campaign;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.CampaignDTO;
import app.scit46.ufc.dto.TagDTO;
import app.scit46.ufc.dto.custom.CreateCampaignDTO;
import app.scit46.ufc.entity.CampaignEntity;
import app.scit46.ufc.entity.TagEntity;
import app.scit46.ufc.repository.campaign.CampaignRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;

    // ================== 기본적인 CRUD 기능 작성 ================== //Start

    // 캠페인 생성
    public void createCampaign(CampaignDTO campaignDTO) {
        CampaignEntity campaign = CampaignEntity.toEntity(campaignDTO);
        campaignRepository.save(campaign);
    }

    // 캠페인 리스트 조회(검색어를 통한 검색 -> 태그/제목 참조)
    public List<CampaignDTO> readCampaignList(String searchKeyword) {
        List<CampaignEntity> campaigns = campaignRepository.findByTitleContainingOrTagsContaining(searchKeyword, searchKeyword);
        return campaigns.stream().map(CampaignDTO::toDTO).collect(Collectors.toList());
    }


    // 캠페인 조회
    public CampaignDTO readCampaign(Long campaignId) {   
        CampaignEntity campaign = campaignRepository.findById(campaignId).orElse(null);
        return CampaignDTO.toDTO(campaign);
    }   

    public CampaignEntity updateCampaign(Long campaignId, CampaignDTO campaignDTO) {
        CampaignEntity campaign = campaignRepository.findById(campaignId).orElse(null);
        if (campaign != null) {
            campaign.setTitle(campaignDTO.getTitle());
            campaign.setDescription(campaignDTO.getDescription());
            campaignRepository.save(campaign);
        }
        return campaign;
    }   

    public void deleteCampaign(Long campaignId) {
        campaignRepository.deleteById(campaignId);
    }
    // ================== 기본적인 CRUD 기능 작성 ================== //End


    public void createCampaign(CreateCampaignDTO ccDTO){
        /*
         * CreateCampaignDTO의 필드드
         * private List<String> tagList; -> List<TagDTO>
         * private String title; -> CampaignDTO
         * private LocalDateTime startDate; -> CampaignDTO
         * private LocalDateTime endDate; -> CampaignDTO
         * private LocalDateTime sendDate; -> CampaignDTO
         * private String description; -> CampaignDTO ??
         * private List<Map<String, Number>> fundingItems; -> RewardMaterialDTO ??
         * private List<Map<String, ?>> rewardList; -> RewardMaterialDTO
         * private String imageUrl; -> ImageDTO
         * private Long imageId; -> ImageDTO
         */
        // 태그 리스트 생성
        List<TagDTO> tagList = ccDTO.getTagList().stream()
                .map(tag -> TagDTO.builder()
                        .content(tag)
                        .build())
                .collect(Collectors.toList());

        // 캠페인 엔티티 생성
        CampaignDTO campaign = CampaignDTO.builder()
                .title(ccDTO.getTitle())
                .description(ccDTO.getDescription())
                .startDate(ccDTO.getStartDate())
                .endDate(ccDTO.getEndDate())
                .sendDate(ccDTO.getSendDate())
                .build();
    }
}
