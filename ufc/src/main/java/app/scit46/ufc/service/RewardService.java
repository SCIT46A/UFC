package app.scit46.ufc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.ItemDTO;
import app.scit46.ufc.dto.MaterialDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.custom.RewardFundingDTO;
import app.scit46.ufc.dto.custom.RewardListDTO;
import app.scit46.ufc.dto.reward.RewardDTO;
import app.scit46.ufc.dto.reward.RewardItemDTO;
import app.scit46.ufc.dto.reward.RewardMaterialDTO;
import app.scit46.ufc.entity.ItemEntity;
import app.scit46.ufc.entity.MaterialEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.reward.RewardEntity;
import app.scit46.ufc.entity.reward.RewardItemEntity;
import app.scit46.ufc.entity.reward.RewardMaterialEntity;
import app.scit46.ufc.repository.RewardItemRepository;
import app.scit46.ufc.repository.RewardRepository;
import app.scit46.ufc.repository.campaign.CampaignRepository;
import app.scit46.ufc.repository.material.RewardMaterialRepository;
import app.scit46.ufc.service.material.MaterialService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RewardService {
    private final RewardRepository rewardRepository;
    private final RewardItemRepository rewardItemRepository;
    private final ItemService itemService;
    private final CampaignRepository campaignRepository;
    private final MaterialService materialService;
    private final RewardMaterialRepository rewardMaterialRepository;
    final private RewardRepository RewardRepository;

    // public RewardEntity addReward(RewardDTO reward) {
    //     return rewardRepository.save(RewardEntity.toEntity(reward));
    // }

    // rewardListDTO : {"name" : "", "amount" : 0, "reward" : [  {"name" : "", "amount" : 0 },...]}
    // rewardFundingDTO : [  {"name" : "", "amount" : 0 }  ,...] ->
    public RewardEntity addReward(RewardListDTO rewardListDTO, Long campaignId) {
        // 리워드 제목, 리워드 갯수제한
        String rewardName = rewardListDTO.getName();
        Integer rewardAmount = rewardListDTO.getAmount();

        // 재료 리스트
        List<RewardFundingDTO> rewardMaterialDTO = rewardListDTO.getFunding();
        // 리워드 리스트
        List<RewardFundingDTO> rewardFundingDTO = rewardListDTO.getReward();

        // 해당 리워드가 속한 캠페인 찾기
        CampaignEntity campaign = campaignRepository.findById(campaignId).orElse(null);

        // 반환값 : 등록된 Reward 그룹 반환
        RewardEntity reward = rewardRepository.save(RewardEntity.builder()
                .campaign(campaign)
                .rewardName(rewardName)
                .amount(rewardAmount)
                .build());

        List<RewardItemEntity> resultList = new ArrayList<>();
        // 리워드 아이템 등록(아이템이 없으면 등록, 있으면 Entity 반환), 갯수는 RewardItemEntity.quantity
        for(RewardFundingDTO rFDTO : rewardFundingDTO) {
            // 아이템 등록(아이템이 없으면 등록 후 반환, 있으면 찾은 후 반환)
            ItemEntity item = itemService.addItem(ItemDTO.builder()
                    .name(rFDTO.getName())
                    .build());

            // 리워드 등록
            RewardItemEntity rewardItem = rewardItemRepository.save(RewardItemEntity.builder()
                    .reward(reward)
                    .item(item)
                    .quantity(rFDTO.getAmount())
                    .build());
            resultList.add(rewardItem);
        }
        reward.setRewardItems(resultList);

        List<RewardMaterialEntity> rewardMaterialList = new ArrayList<>();
        // 재료 등록
        for(RewardFundingDTO rFDTO : rewardMaterialDTO) {
            // 재료 등록(재료가 없으면 등록, 있으면 찾은 후 반환)
            MaterialEntity material = materialService.addMaterial(MaterialDTO.builder()
                    .name(rFDTO.getName())
                    .build());

            // 리워드에 대한 재료 등록
            RewardMaterialEntity rewardMaterial = rewardMaterialRepository.save(RewardMaterialEntity.builder()
                    .reward(reward)
                    .material(material)
                    .quantityRequired(rFDTO.getAmount())
                    .build());
            rewardMaterialList.add(rewardMaterial);
        }
        reward.setRewardMaterials(rewardMaterialList);

        // 리워드 그룹 등록
        return rewardRepository.save(reward);
    }

    public List<RewardDTO> getRewards(Long campaignId) {
        // 예시: RewardEntity 리스트를 조회 후 DTO로 변환
        List<RewardEntity> rewardEntities = RewardRepository.findAllByCampaign_CampaignId(campaignId);
        return rewardEntities.stream()
                .map(RewardDTO::toDTO)
                .collect(Collectors.toList());
    }

    public RewardDTO getReward(Long rewardId) {
        RewardEntity rewardEntity = rewardRepository.findById(rewardId).orElse(null);
        return RewardDTO.toDTO(rewardEntity);

    }

}
