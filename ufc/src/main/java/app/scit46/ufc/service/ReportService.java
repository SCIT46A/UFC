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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository; // ✅ 유저 데이터 접근 추가

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    // ✅ 모든 유저 신고 조회
    public List<ReportDTO> getAllReportedUsers() {
        return reportRepository.findAll().stream()
                .filter(report -> report.getUser() != null) // ✅ 유저 신고만 반환
                .map(ReportDTO::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 캠페인 신고 목록 조회
    public List<Map<String, Object>> getReportedCampaigns() {
        return reportRepository.findAllReportsWithCampaignTitle().stream()
                .map(obj -> {
                    ReportEntity report = (ReportEntity) obj[0]; // ✅ report 테이블 데이터
                    String campaignTitle = (String) obj[1]; // ✅ campaign 테이블에서 가져온 title

                    // ✅ 기존 DTO 데이터를 Map으로 변환 후 `campaignTitle` 추가
                    Map<String, Object> response = new HashMap<>();
                    response.put("campaignId", report.getCampaign().getCampaignId()); // 캠페인 ID
                    response.put("title", campaignTitle); // ✅ 캠페인 제목 추가
                    response.put("reason", report.getReason()); // 신고 이유
                    response.put("reportedBy", report.getReportedBy()); // 신고자 ID
                    response.put("reportedDate", report.getReportedDate()); // 신고 날짜

                    return response;
                })
                .collect(Collectors.toList());
    }

    //유저 신고
    @Transactional
    public void processUserReport(Long reportId, String action, String reason) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("해당 신고가 존재하지 않습니다."));
        UserEntity user = userRepository.findById(report.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        // ✅ 기존 값 확인
        System.out.println("🔍 변경 전 statusReason: " + user.getStatusReason());

        if ("rejected".equals(action)) {
            report.setStatus("rejected");
            user.setStatusReason(reason); // ✅ 신고 사유 저장
        } else {
            int daysToBan = Integer.parseInt(action);
            user.setUserStatus(0);
            user.setUpdatedAt(LocalDateTime.now().plusDays(daysToBan));
            user.setStatusReason(reason); // ✅ 신고 사유 저장
            report.setStatus("ok");
        }

        // ✅ 값 변경 후 로그 확인
        System.out.println("🔄 변경된 statusReason: " + user.getStatusReason());

        reportRepository.save(report);
        userRepository.save(user);  // ✅ 변경 사항 저장

        // ✅ 저장 후 최종 확인
        System.out.println("✅ 최종 저장된 statusReason: " + user.getStatusReason());
    }


    // ✅ 유저 정지 해제 기능 (매일 00시 실행)
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void unbanExpiredUsers() {
        List<UserEntity> bannedUsers = userRepository.findAllByUserStatus(0); // ✅ 정지된 유저 찾기

        for (UserEntity user : bannedUsers) {
            if (user.getUpdatedAt() != null && user.getUpdatedAt().isBefore(LocalDateTime.now())) {
                user.setUserStatus(1);  // ✅ 정지 해제
                user.setUpdatedAt(null); // ✅ 정지 해제 날짜 초기화
                user.setStatusReason(null); // ✅ 신고 사유 초기화

                userRepository.save(user); // ✅ 변경 사항 저장

                // ✅ 로그 추가 (정지 해제된 유저 확인)
                System.out.println("✅ 유저 정지 해제됨 - User ID: " + user.getUserId());
            }
        }
    }

}
