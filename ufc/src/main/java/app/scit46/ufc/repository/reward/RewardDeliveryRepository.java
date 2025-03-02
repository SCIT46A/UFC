package app.scit46.ufc.repository.reward;

import org.springframework.data.jpa.repository.JpaRepository;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import java.util.List;

public interface RewardDeliveryRepository extends JpaRepository<RewardDeliveryEntity, Long> {
    List<RewardDeliveryEntity> findByCampaign_CampaignIdInAndStatus(List<Long> campaignIds, String status);

    List<RewardDeliveryEntity> findByCampaign_CampaignIdIn(List<Long> campaignIds);
}
