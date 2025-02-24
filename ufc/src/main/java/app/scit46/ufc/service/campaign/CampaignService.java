package app.scit46.ufc.service.campaign;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.dto.ItemDTO;
import app.scit46.ufc.dto.MaterialDTO;
import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.campaign.CampaignGoalDTO;
import app.scit46.ufc.dto.custom.FundingDTO;
import app.scit46.ufc.dto.custom.GenerateCampaignDTO;
import app.scit46.ufc.dto.custom.RewardListDTO;
import app.scit46.ufc.dto.reward.RewardDTO;
import app.scit46.ufc.dto.reward.RewardMaterialDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.ImageUrlEntity;
import app.scit46.ufc.entity.ItemEntity;
import app.scit46.ufc.entity.MaterialEntity;
import app.scit46.ufc.entity.TagEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.campaign.CampaignTagEntity;
import app.scit46.ufc.entity.reward.RewardEntity;
import app.scit46.ufc.entity.reward.RewardMaterialEntity;
import app.scit46.ufc.repository.CampaignGoalRepository;
import app.scit46.ufc.repository.CreatorRepository;
import app.scit46.ufc.repository.MaterialDonationRepository;
import app.scit46.ufc.repository.UserRepository;
import app.scit46.ufc.repository.campaign.CampaignRepository;
import app.scit46.ufc.repository.tag.CampaignTagRepository;
import app.scit46.ufc.service.CreatorService;
import app.scit46.ufc.service.ImageUrlService;
import app.scit46.ufc.service.ItemService;
import app.scit46.ufc.service.RewardService;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.cloudflare.ImageService;
import app.scit46.ufc.service.material.MaterialService;
import app.scit46.ufc.service.material.RewardMaterialService;
import app.scit46.ufc.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CreatorService creatorService;
    private final UserService userService;
    private final TagService tagService;
    private final CampaignTagRepository campaignTagRepository;
    private final MaterialService materialService;
    private final RewardMaterialService rewardMaterialService;
    private final CampaignGoalRepository campaignGoalRepository;
    private final MaterialDonationRepository materialDonationRepository;
    private final ImageUrlService imageService;
    private final ItemService itemService;
    private final RewardService rewardService;

    // ================== 기본적인 CRUD 기능 작성 ================== //Start

    // 캠페인 리스트 조회(검색어를 통한 검색 -> 태그/제목 참조)
    public List<CampaignDTO> readCampaignList(String searchKeyword) {
        List<CampaignEntity> campaigns = campaignRepository.findByTitleContaining(searchKeyword); //임시조치
        //List<CampaignEntity> campaigns = campaignRepository.findByTitleContainingOrTagsContaining(searchKeyword, searchKeyword);
        return campaigns.stream().map(CampaignDTO::toDTO).collect(Collectors.toList());
    }

    // 캠페인 조회
    public CampaignDTO readCampaign(Long campaignId) {   
        CampaignEntity campaign = campaignRepository.findById(campaignId).orElse(null);
        return CampaignDTO.toDTO(campaign);
    }   

    // 캠페인 수정 / 캠페인 아이디와 캠페인 요소 받아서 수정
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
    
    // 캠페인 생성
    public Long createCampaign(CampaignDTO campaignDTO, CreatorEntity creator, ImageUrlEntity image) {
        // 캠페인 엔티티 생성(변환로직은 CampaignEntity.toEntity() 참조)
        CampaignEntity campaign = CampaignEntity.toEntity(campaignDTO);
        // 영속성 문제로 인한 창작자, 이미지 자체 설정
        campaign.setCreatedBy(creator);
        campaign.setPhoto(image);
        // 캠페인 엔티티 저장 후 캠페인 아이디 반환
        return campaignRepository.save(campaign).getCampaignId();
    }

    // ================== 기본적인 CRUD 기능 작성 ================== //End

    // GenerateCampaign -> 
    @Transactional
    public Long createCampaign(GenerateCampaignDTO ccDTO){
        /*
         * CreateCampaignDTO의 필드드
         * private List<String> tagList; -> List<TagDTO>
         * private String title; -> CampaignDTO
         * private LocalDateTime startDate; -> CampaignDTO
         * private LocalDateTime endDate; -> CampaignDTO
         * private LocalDateTime sendDate; -> CampaignDTO
         * private String description; -> CampaignDTO
         * private List<Map<String, Number>> fundingItems; -> RewardMaterialDTO ??
         * private List<Map<String, ?>> rewardList; -> RewardMaterialDTO
         * private String imageUrl; -> ImageDTO xx
         * private Long imageId; -> ImageDTO xx
         */

        // log.info("ccDTO.getUserName() : {}", ccDTO.getUserName().trim());
        // log.info("DB 사용자 조회 결과 : {}", userRepository.findByUserName(ccDTO.getUserName().trim()));

        // 사용자 이름으로 창작자 아이디 조회(UserEntity.userName -> CreatorEntity.ownUser ->
        // UserEntity.userId -> CreatorEntity.creatorId)
        UserEntity user = userService.findUserByUserName(ccDTO.getUserName().trim());
        // UserEntity user = userRepository.findByUserName(
        //         ccDTO.getUserName().trim()).orElseThrow(() -> new RuntimeException("창작자를 찾을 수 없습니다.") // Optional                                                                                         // 처리
        // );
        CreatorEntity creator = creatorService.findByOwnUser(user);
        // log.info("창작자 아이디 : {}", creatorId);
        
        // 이미 저장처리된 이미지 아이디 조회
        ImageUrlEntity image = imageService.findByImageId(ccDTO.getImageId());

        // 캠페인 엔티티 생성
        CampaignDTO campaign = CampaignDTO.builder()
                .title(ccDTO.getTitle())
                .description(ccDTO.getDescription())
                .startDate(ccDTO.getStartDate())
                .endDate(ccDTO.getEndDate())
                .sendDate(ccDTO.getSendDate())
                .campaignStatus(false)
                .build();

        // 캠페인 생성 및 캠페인 아이디 반환
        Long campaignId = createCampaign(campaign, creator, image);

        // 태그 리스트 생성
        // List<TagDTO> tagList = ccDTO.getTagList().stream()
        //         .map(tag -> TagDTO.builder()
        //                 .content(tag)
        //                 .build())
        //         .collect(Collectors.toList());

        // 지정된 태그를 저장/조회 후 태그 아이디 리스트 반환
        List<Integer> tagIds = tagService.saveAndFindTagIds(ccDTO.getTagList());

        // 태그 아이디와 캠페인 아이디를 CampaignTagEntity(태그 아이디와 캠페인 아이디를 연결하는 엔티티)에 저장
        for (Integer tagId : tagIds) {
            CampaignTagEntity campaignTag = CampaignTagEntity.builder()
                    .campaign(CampaignEntity.builder().campaignId(campaignId).build())
                    .tag(TagEntity.builder().tagId(tagId).build())
                    .build();

            campaignTagRepository.save(campaignTag);
        }

        // List<String> materialNames = new ArrayList<>(ccDTO.getFundingItems().keySet());
        // List<Long> materialIds = materialService.addMaterial(materialNames);

        // List<FundingDTO> fundingItems = ccDTO.getFundingItems().entrySet().stream()
        //         .map(entry -> new FundingDTO(entry.getKey(), entry.getValue()))
        //         .collect(Collectors.toList());

        // log.info("fundingItems : {}", fundingItems);

        // // 펀딩 아이템 생성 [{"name": amount},...]
        // for (RewardListDTO fundingItem : fundingItems) {
            // String fundingName = fundingItem.getName();
            // int fundingAmount = fundingItem.getAmount();

            // // 펀딩 아이템 생성
            // Map<String, Integer> materialDonation = MaterialDonationDTO.builder()
            //         .campaign(CampaignEntity.builder().campaignId(campaignId).build())
            //         .material(MaterialEntity.builder().materialId(materialIds.get(0)).build())
            //         .build();

            // rewardMaterialService.addRewardMaterial(rewardMaterial);
        // }

        // // 리워드 생성   [{"name": "", "amount": 0 , "funding" : [{"name" : "", "amount" : 0 },...]},...]
        for (RewardListDTO rewardList : ccDTO.getRewardList()) {
            ItemEntity item = itemService.addItem(ItemDTO.builder()
                    .name(rewardList.getName())
                    .build());

            RewardEntity reward = rewardService.addReward(RewardEntity.builder()
                    .campaign(CampaignEntity.builder().campaignId(campaignId).build())
                    .item(item)
                    .amount(rewardList.getAmount())
                    .build());

            for(FundingDTO funding : rewardList.getFunding()) {
                MaterialEntity material = materialService.addMaterial(MaterialDTO.builder()
                        .name(funding.getName())
                        .build());

                RewardMaterialEntity rewardMaterial = rewardMaterialService.addRewardMaterial(RewardMaterialEntity.builder()
                        .reward(reward)
                        .material(material)
                        .quantityRequired(funding.getAmount())
                        .build());
            }
        }

        for (Integer tagId : tagIds) {
            CampaignTagEntity campaignTag = CampaignTagEntity.builder()
                    .campaign(CampaignEntity.builder().campaignId(campaignId).build())
                    .tag(TagEntity.builder().tagId(tagId).build())
                    .build();

            campaignTagRepository.save(campaignTag);
        }

        return campaignId;
    }

    public void editCampaign(Long campaignId, GenerateCampaignDTO ccDTO){
        CampaignDTO campaign = CampaignDTO.builder()
                .title(ccDTO.getTitle())
                .description(ccDTO.getDescription())
                .startDate(ccDTO.getStartDate())
                .endDate(ccDTO.getEndDate())
                .sendDate(ccDTO.getSendDate())
                .build();

        updateCampaign(campaignId, campaign);
    }


    public List<CampaignEntity> campaignFindByCampaignId(Long campaignId) {
      return campaignRepository.findByCampaignId(campaignId);
    }

    //펀딩 대기 중인
    public List<CampaignDTO> getFundingWaitingCampaigns() {
        LocalDateTime now = LocalDateTime.now();
        return campaignRepository.findByCampaignStatusAndStartDateAfter(true, now)
                .stream()
                .map(CampaignDTO::toDTO) // ✅ modelMapper 대신 직접 변환
                .collect(Collectors.toList());
    }



    // ✅ 승인 대기 중인 캠페인 조회 (N+1 문제 해결)
    @Transactional(readOnly = true)
    public List<CampaignDTO> getPendingCampaigns() {
        return campaignRepository.findByPendingApproval().stream()
                .map(CampaignDTO::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 개별 캠페인 승인 (Dirty Checking 활용)
    @Transactional
    public void approveCampaign(Long campaignId) {
        CampaignEntity campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("캠페인을 찾을 수 없습니다."));
        campaign.setCampaignStatus(true);
        campaignRepository.save(campaign); // ✅ 변경 사항 저장 필요!
    }


    // ✅ 여러 개의 캠페인 승인 (일괄 처리 추가)
    @Transactional
    public void approveMultipleCampaigns(List<Long> campaignIds) {
        List<CampaignEntity> campaigns = campaignRepository.findAllById(campaignIds);

        if (campaigns.isEmpty()) {
            throw new RuntimeException("선택된 캠페인을 찾을 수 없습니다.");
        }

        for (CampaignEntity campaign : campaigns) {
            campaign.setCampaignStatus(true);
        }

        campaignRepository.saveAll(campaigns);
    }

    // ✅ 전체 캠페인 조회 (N+1 문제 해결)
    @Transactional(readOnly = true)
    public List<CampaignDTO> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .map(CampaignDTO::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 캠페인 목표 조회 (CampaignGoalEntity → CampaignGoalDTO 변환)
    @Transactional(readOnly = true)
    public List<CampaignGoalDTO> getAllCampaignGoals() {
        return campaignGoalRepository.findAll().stream()
                .map(CampaignGoalDTO::toDTO)  // Entity → DTO 변환
                .collect(Collectors.toList());
    }

    // ✅ 캠페인 기부 내역 조회 (MaterialDonationEntity → MaterialDonationDTO 변환)
    @Transactional(readOnly = true)
    public List<MaterialDonationDTO> getAllMaterialDonations() {
        return materialDonationRepository.findAll().stream()
                .map(MaterialDonationDTO::toDTO)  // Entity → DTO 변환
                .collect(Collectors.toList());
    }
}
