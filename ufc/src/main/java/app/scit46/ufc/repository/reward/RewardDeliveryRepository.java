package app.scit46.ufc.repository.reward;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import java.util.Optional;

public interface RewardDeliveryRepository extends JpaRepository<RewardDeliveryEntity, Long> {

    //
    @Query("SELECT r FROM RewardDeliveryEntity r WHERE r.reward.campaign.campaignId IN :campaignIds")
    List<RewardDeliveryEntity> findByCampaignIdIn(@Param("campaignIds") List<Long> campaignIds);

    Optional<RewardDeliveryEntity> findByDonation_DonationId(Long donationId);
}
