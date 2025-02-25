package app.scit46.ufc.service;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.ItemDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.custom.RewardFundingDTO;
import app.scit46.ufc.dto.reward.RewardDTO;
import app.scit46.ufc.entity.ItemEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.reward.RewardEntity;
import app.scit46.ufc.entity.reward.RewardItemEntity;
import app.scit46.ufc.repository.RewardItemRepository;
import app.scit46.ufc.repository.RewardRepository;
import app.scit46.ufc.service.campaign.CampaignService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class RewardService {
    private final RewardRepository rewardRepository;
    private final RewardItemRepository rewardItemRepository;
    private final ItemService itemService;
    private final CampaignService campaignService;

    public RewardEntity addReward(RewardDTO reward) {
        return rewardRepository.save(RewardEntity.toEntity(reward));
    }

    // "reward" : [  {"name" : "", "amount" : 0 }  ,...]
    public RewardItemEntity addRewardItem(RewardFundingDTO rewardFundingDTO, Long campaignId) {
        // 리워드 아이템 등록(아이템이 없으면 등록, 있으면 Entity 반환)
        ItemEntity item = itemService.addItem(ItemDTO.builder()
                .name(rewardFundingDTO.getName())
                .build());

        CampaignEntity campaign = campaignService.getCampaign(campaignId);

        // 리워드 등록
        RewardEntity reward = addReward(RewardDTO.builder()
                .campaign(CampaignDTO.toDTO(campaign))
                .rewardItems(item.)
                .amount(rewardFundingDTO.getAmount())
                .build());

        return rewardItemRepository.save(rewardItem);
    }
}
