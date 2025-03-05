package app.scit46.ufc.repository.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.campaign.CampaignTagEntity;

public interface CampaignTagRepository extends JpaRepository<CampaignTagEntity, Long> {

    List<CampaignTagEntity> findByCampaign(CampaignEntity campaign);

    List<CampaignTagEntity> findTagsByCampaign_CampaignId(Long campaignId);

    @Query("SELECT ct FROM CampaignTagEntity ct " +
            "JOIN FETCH ct.campaign c " + // 캠페인과 함께 가져옴
            "JOIN FETCH ct.tag t " + // 태그와 함께 가져옴
            "WHERE c.campaignId = :campaignId")
    List<CampaignTagEntity> findTagsByCampaignId(@Param("campaignId") Long campaignId);


}
