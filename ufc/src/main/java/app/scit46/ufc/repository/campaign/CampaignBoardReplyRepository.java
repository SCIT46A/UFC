package app.scit46.ufc.repository.campaign;

import app.scit46.ufc.entity.campaign.CampaignBoardReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignBoardReplyRepository extends JpaRepository<CampaignBoardReplyEntity,Long> {
    @Query("select r from CampaignBoardReplyEntity r where r.campaignBoard.cBoardId = :cBoardId")
    List<CampaignBoardReplyEntity> findAllByCampaignBoardCBoardId(@Param("cBoardId") Long cBoardId);

}
