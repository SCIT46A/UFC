package app.scit46.ufc.repository.campaign;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.campaign.CampaignBoardEntity;

@Repository
public interface CampaignBoardRepository extends JpaRepository<CampaignBoardEntity, Long> {

    List<CampaignBoardEntity> findAllByCampaign_CampaignId(Long campaignId);
}
