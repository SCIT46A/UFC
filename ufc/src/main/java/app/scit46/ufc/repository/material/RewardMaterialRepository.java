package app.scit46.ufc.repository.material;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.reward.RewardEntity;
import app.scit46.ufc.entity.reward.RewardMaterialEntity;

public interface RewardMaterialRepository extends JpaRepository<RewardMaterialEntity, Long> {

    void deleteByReward(CampaignEntity campaignEntity);

    void deleteByReward(RewardEntity reward);
    
}
