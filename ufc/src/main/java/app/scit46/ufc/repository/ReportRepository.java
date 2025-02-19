package app.scit46.ufc.repository;

import app.scit46.ufc.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    // 특정 상태별 신고 조회 (예: "처리중" 신고 목록만 가져오기)
    List<ReportEntity> findByStatus(String status);

    // 특정 유저가 신고당한 목록 조회
    List<ReportEntity> findByUser_UserId(Long userId);

    // ✅ 신고된 캠페인 목록 조회 추가
    List<ReportEntity> findByCampaignIsNotNull();
}