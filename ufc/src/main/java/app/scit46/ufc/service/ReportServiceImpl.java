package app.scit46.ufc.service;

import app.scit46.ufc.dto.ReportDTO;
import app.scit46.ufc.entity.ReportEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.ReportRepository;
import app.scit46.ufc.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository; // ✅ 유저 데이터 접근 추가

    public ReportServiceImpl(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ReportDTO> getAllReportedUsers() {
        return reportRepository.findAll().stream()
                .map(ReportDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportDTO> getReportsByStatus(String status) {
        return reportRepository.findByStatus(status).stream()
                .map(ReportDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportDTO> getReportsByUserId(Long userId) {
        return reportRepository.findByUser_UserId(userId).stream()
                .map(ReportDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportDTO> getReportedCampaigns() {
        return reportRepository.findByCampaignIsNotNull().stream()
                .map(ReportDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateReportStatus(Long reportId, String status) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("해당 신고가 존재하지 않습니다."));
        report.setStatus(status);
        reportRepository.save(report);
    }

    // ✅ 유저 신고 조치 기능 추가
    @Transactional
    @Override
    public void processUserReport(Long reportId, String action, String reason) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("해당 신고가 존재하지 않습니다."));
        UserEntity user = userRepository.findById(report.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        if ("rejected".equals(action)) {
            // 반려 처리
            report.setStatus("rejected");
        } else {
            // 정지 처리 (3일, 5일, 영구)
            int daysToBan = Integer.parseInt(action);
            user.setUserStatus(0); // 유저 계정 정지
            user.setUpdatedAt(LocalDateTime.now().plusDays(daysToBan)); // 정지 기간 설정
            report.setStatus("ok"); // 신고 처리 완료 상태 변경
        }

        reportRepository.save(report);
        userRepository.save(user);
    }
}
