package app.scit46.ufc.repository;

import app.scit46.ufc.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    // ✅ `UserEntity.userId`를 직접 선택해서 가져옴 (UserEntity 전체가 아니라 ID만!)
    @Query("SELECT r.reportId, r.campaign.campaignId, c.title, r.reason, r.reportedBy.userId, r.reportedDate, r.status " +
            "FROM ReportEntity r " +
            "JOIN r.campaign c " +  // ✅ `campaign` 테이블과 조인
            "WHERE r.campaign IS NOT NULL")
    List<Object[]> findAllReportsWithCampaignTitle();
}



