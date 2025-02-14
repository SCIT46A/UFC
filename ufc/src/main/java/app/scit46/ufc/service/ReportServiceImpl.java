package app.scit46.ufc.service;

import app.scit46.ufc.dto.ReportDTO;
import app.scit46.ufc.entity.ReportEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.ReportRepository;
import app.scit46.ufc.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
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
                .filter(report -> report.getUser() != null) // ✅ 유저 신고만 반환
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

    // ✅ 유저 신고 조치 기능 추가 (userStatus = int 타입으로 변경)
    @Transactional
    @Override
    public void processUserReport(Long reportId, String action, String reason) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("해당 신고가 존재하지 않습니다."));
        UserEntity user = userRepository.findById(report.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        if ("rejected".equals(action)) {
            report.setStatus("rejected");
            user.setStatusReason(reason); // ✅ 보류 사유 저장
        } else {
            int daysToBan = Integer.parseInt(action);
            user.setUserStatus(0);
            user.setUpdatedAt(LocalDateTime.now().plusDays(daysToBan));
            user.setStatusReason(reason); // ✅ 정지 사유 저장
            report.setStatus("ok");
        }

        System.out.println("🔍 저장될 Status Reason: " + user.getStatusReason());

        reportRepository.save(report);
        userRepository.save(user);
    }







    @Override
    @Transactional
    public void saveReport(ReportEntity report) {
        if (!report.isValid()) {
            throw new IllegalArgumentException("🚨 신고는 user_id, campaign_id, product_id 중 최소 하나 이상 포함해야 합니다.");
        }
        reportRepository.save(report);
    }




    // ✅ 유저 정지 해제 기능 (매일 00시 실행)
    @Scheduled(cron = "0 0 0 * * ?") // 매일 00시 실행
    public void unbanExpiredUsers() {
        List<UserEntity> bannedUsers = userRepository.findAllByUserStatus(0); // ✅ 여기서 userStatus 사용

        for (UserEntity user : bannedUsers) {
            if (user.getUpdatedAt() != null && user.getUpdatedAt().isBefore(LocalDateTime.now())) {
                // 🚨 정지 해제 (user_status = 1, updated_at 초기화)
                user.setUserStatus(1);
                user.setUpdatedAt(null);
                userRepository.save(user);
            }
        }

        System.out.println("✅ 만료된 유저 정지가 해제되었습니다.");
    }
}