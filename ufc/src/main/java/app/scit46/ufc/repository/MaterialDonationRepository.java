package app.scit46.ufc.repository;

import app.scit46.ufc.entity.MaterialDonationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialDonationRepository extends JpaRepository<MaterialDonationEntity, Long> {

    @Query("SELECT d FROM MaterialDonationEntity d JOIN FETCH d.campaign")
    List<MaterialDonationEntity> findAllWithCampaign();
}
