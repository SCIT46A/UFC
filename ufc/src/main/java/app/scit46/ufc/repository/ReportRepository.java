package app.scit46.ufc.repository;

import app.scit46.ufc.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    @Query("SELECT r.reportId, c.campaignId, c.title, r.reason, r.reportedBy.userId, r.reportedDate, r.status, c.createdBy.creatorId " +
            "FROM ReportEntity r " +
            "JOIN r.campaign c " +
            "JOIN c.createdBy cr")

    List<Object[]> findAllReportsWithCampaignTitle();

}



