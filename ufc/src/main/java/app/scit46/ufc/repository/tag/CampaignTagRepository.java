package app.scit46.ufc.repository.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import app.scit46.ufc.entity.campaign.CampaignTagEntity;

public interface CampaignTagRepository extends JpaRepository<CampaignTagEntity, Long> {

    List<CampaignTagEntity> findByCampaignId(Long campaignId);
    
}
