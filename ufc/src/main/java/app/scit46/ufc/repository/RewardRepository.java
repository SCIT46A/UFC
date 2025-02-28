package app.scit46.ufc.repository;

import app.scit46.ufc.entity.reward.RewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<RewardEntity, Long> {
    List<RewardEntity> findAllByCampaign_CampaignId(Long campaignId);
}
