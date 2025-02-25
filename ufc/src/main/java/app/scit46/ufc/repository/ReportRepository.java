package app.scit46.ufc.repository;

import app.scit46.ufc.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    
        @Query("SELECT r, c.title FROM ReportEntity r " +
                "JOIN r.campaign c " +
                "WHERE r.campaign IS NOT NULL")
        List<Object[]> findAllReportsWithCampaignTitle();
}



