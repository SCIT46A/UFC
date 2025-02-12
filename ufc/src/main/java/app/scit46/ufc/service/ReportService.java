package app.scit46.ufc.service;

import app.scit46.ufc.dto.ReportDTO;
import java.util.List;

public interface ReportService {
    List<ReportDTO> getAllReportedUsers();
    List<ReportDTO> getReportsByStatus(String status);
    List<ReportDTO> getReportsByUserId(Long userId);
    List<ReportDTO> getReportedCampaigns(); // ✅ 캠페인 신고 목록 조회 추가
    void updateReportStatus(Long reportId, String status);
    void processUserReport(Long reportId, String action, String reason); // ✅ 유저 신고 조치 추가
}
