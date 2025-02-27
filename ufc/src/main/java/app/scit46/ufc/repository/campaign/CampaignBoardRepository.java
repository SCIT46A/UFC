package app.scit46.ufc.repository.campaign;

import app.scit46.ufc.dto.campaign.CampaignBoardDTO;
import app.scit46.ufc.entity.campaign.CampaignBoardEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignBoardRepository extends JpaRepository<CampaignBoardEntity, Long> {

    List<CampaignBoardEntity> findAllByCampaign_CampaignId(Long campaignId);
}
