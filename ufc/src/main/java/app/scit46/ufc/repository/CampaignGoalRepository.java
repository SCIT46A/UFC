package app.scit46.ufc.repository;

import app.scit46.ufc.entity.CampaignGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignGoalRepository extends JpaRepository<CampaignGoalEntity, Long> {

    @Query("SELECT g FROM CampaignGoalEntity g JOIN FETCH g.campaign")
    List<CampaignGoalEntity> findAllWithCampaign();
}
