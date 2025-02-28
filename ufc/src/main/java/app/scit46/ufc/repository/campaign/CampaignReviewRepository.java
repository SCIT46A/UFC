package app.scit46.ufc.repository.campaign;

import app.scit46.ufc.entity.campaign.CampaignReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignReviewRepository extends JpaRepository<CampaignReviewEntity, Long> {
    List<CampaignReviewEntity> findAllByCampaignedBy_CampaignId(Long campaignId);
}
