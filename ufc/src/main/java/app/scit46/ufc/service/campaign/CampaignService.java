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
import app.scit46.ufc.dto.campaign.CampaignTagDTO;
import app.scit46.ufc.dto.custom.RewardFundingDTO;
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
import app.scit46.ufc.entity.campaign.CampaignGoalEntity;
import app.scit46.ufc.entity.campaign.CampaignTagEntity;
import app.scit46.ufc.entity.reward.RewardEntity;
import app.scit46.ufc.entity.reward.RewardItemEntity;
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
    private final CampaignGoalRepository campaignGoalRepository;
    private final MaterialService materialService;
    private final MaterialDonationRepository materialDonationRepository;
    private final ImageUrlService imageService;
    private final RewardService rewardService;

    public CampaignEntity findCampaignById(Long id) {
        return campaignRepository.findById(id).orElse(null);
    }

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

    public CampaignEntity getCampaign(Long campaignId) {
        CampaignEntity campaign = campaignRepository.findById(campaignId).orElse(null);
        return campaign;
    }

    // 캠페인 수정 / 캠페인 아이디와 캠페인 요소 받아서 수정
    public CampaignEntity updateCampaign(CampaignDTO campaignDTO) {
        CampaignEntity campaign = campaignRepository.findById(campaignDTO.getCampaignId()).orElse(null);
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
    public CampaignEntity createCampaign(CampaignDTO campaignDTO, CreatorEntity creator, ImageUrlEntity image) {
        // 캠페인 엔티티 생성(변환로직은 CampaignEntity.toEntity() 참조)
        CampaignEntity campaign = CampaignEntity.toEntity(campaignDTO);
        // 영속성 문제로 인한 창작자, 이미지 자체 설정
        campaign.setCreatedBy(creator);
        campaign.setPhoto(image);
        // 캠페인 엔티티 저장 후 캠페인 아이디 반환
        return campaignRepository.save(campaign);
    }

    // ================== 기본적인 CRUD 기능 작성 ================== //End

    // GenerateCampaign = 프론트에서 보낸 데이터를 받아줄 커스텀 DTO -> 파싱하여 적절히 처리하는 로직
    // Entity를 그대로 사용한 것은 영속성 문제로 인해 캠페인 생성에 필요한 세부 요소들의 연계에 오류가 발생하기 때문
    @Transactional
    public Long createCampaign(GenerateCampaignDTO ccDTO){

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
                .isSuccess(false)
                .build();

        // 캠페인 생성 및 캠페인 아이디 반환
        CampaignEntity campaignEntity = createCampaign(campaign, creator, image);

        // 태그 저장(검색) 후 캠페인과 연결
        tagService.linkCampaignTags(ccDTO.getTagList(), campaignEntity);

        // 캠페인 총 목표 재료, 수량정보 저장
        ccDTO.getFundingItems().forEach(funding -> {
            // 재료 등록
            MaterialEntity material = materialService.addMaterial(MaterialDTO.builder()
                    .name(funding.getName())
                    .build());
            // 캠페인 총 목표 재료, 수량정보 저장
            campaignGoalRepository.save(CampaignGoalEntity.builder()
                    .campaign(campaignEntity)
                    .material(material)
                    .quantityRequired(funding.getAmount())
                    .build());
        });

        // 리워드 리스트
        List<RewardEntity> rewardList = new ArrayList<>();
        // 리워드 생성   [{"name": "리워드 제목", "amount": 0 , "funding" : [{"name" : "", "amount" : 0 },...],"reward" : [{"name" : "", "amount" : 0 },...]},...]
        for (RewardListDTO receivedRewardDTO : ccDTO.getRewardList()) {

            // 리워드 아이템, 재료 등록
            RewardEntity reward = rewardService.addReward(receivedRewardDTO, campaignEntity.getCampaignId());
            rewardList.add(reward);

            // // 제공할 리워드 아이템 등록 reward
            // for(RewardFundingDTO rewardDTO : receivedRewardDTO.getReward()) {

            //     RewardItemEntity rewardItemEntity = rewardService.addRewardItem(receivedRewardDTO, rewardDTO, campaignId);
            // }

            // // 리워드 아이템에 필요한 재료 등록 funding
            // for(RewardFundingDTO funding : receivedRewardDTO.getFunding()) {
            //     MaterialEntity material = materialService.addMaterial(funding.getName());

            //     RewardMaterialEntity rewardMaterial = rewardMaterialService.addRewardMaterial(RewardMaterialEntity.builder()
            //             .reward(reward)
            //             .material(material)
            //             .quantityRequired(funding.getAmount())
            //             .build());
            // }

            // RewardDTO rewardDTO = RewardDTO.builder()
            //         .name(rewardName)
            //         .amount(rewardAmount)
            //         .build();
            // rewardList.add(rewardDTO);
        }

        return campaignEntity.getCampaignId();
    }

    // 캠페인 수정(캠페인 생성 로직 + 대상 캠페인 아이디로 다시저장하는 로직)
    public void editCampaign(CampaignDTO campaignDTO, GenerateCampaignDTO ccDTO) {
        CampaignDTO campaign = CampaignDTO.builder()
                .campaignId(campaignDTO.getCampaignId())
                .title(ccDTO.getTitle())
                .description(ccDTO.getDescription())
                .startDate(ccDTO.getStartDate())
                .endDate(ccDTO.getEndDate())
                .sendDate(ccDTO.getSendDate())
                .build();

        updateCampaign(campaign);
    }

    // 캠페인에 할당되어 있는 태그 조회
    public List<String> getCampaignTags(Long campaignId){
        List<CampaignTagEntity> campaignTag = campaignTagRepository.findByCampaign(campaignRepository.findById(campaignId).orElse(null));
        List<String> tagNames = campaignTag.stream()
                .map(CampaignTagEntity::getTag)
                .map(TagEntity::getContent)
                .collect(Collectors.toList());
        return tagNames;
    }

    // 캠페인 아이디로 캠페인 조회
    public List<CampaignEntity> campaignFindByCampaignId(Long campaignId) {
        return campaignRepository.findByCampaignId(campaignId);
    }

    //펀딩 대기 중인 캠페인 조회
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

    // 프론트 캠페인 수정용 리워드, 펀딩재료 데이터형식으로 변환
    public List<RewardListDTO> convertCampaignFundingAndRewards(Long id) {
        CampaignEntity campaign = campaignRepository.findById(id).orElse(null);
        if (campaign == null) {
            throw new RuntimeException("캠페인을 찾을 수 없습니다.");
        }

        List<RewardListDTO> rewardList = new ArrayList<>();
        List<RewardEntity> rewards = campaign.getRewards();
        for (RewardEntity reward : rewards) {
            String name = reward.getRewardName();
            Integer amount = reward.getAmount();
            List<RewardFundingDTO> fundingList = new ArrayList<>();
            for (RewardMaterialEntity rewardMaterial : reward.getRewardMaterials()) {
                String materialName = rewardMaterial.getMaterial().getName();
                Integer quantityRequired = rewardMaterial.getQuantityRequired();
                RewardFundingDTO funding = RewardFundingDTO.builder()
                        .name(materialName)
                        .amount(quantityRequired)
                        .build();
                fundingList.add(funding);
            }
            RewardListDTO rewardListDTO = RewardListDTO.builder()
                    .name(name)
                    .amount(amount)
                    .funding(fundingList)
                    .build();
            rewardList.add(rewardListDTO);
        }
        return rewardList;
    }

    public CampaignDTO getCampaignById(Long id) {
        CampaignEntity campaign = campaignRepository.findById(id).orElse(null);
        return CampaignDTO.toDTO(campaign);
    }
}