package app.scit46.ufc.service;

import app.scit46.ufc.dto.ReportDTO;
import app.scit46.ufc.entity.ReportEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.ReportRepository;
import app.scit46.ufc.repository.UserRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
                    Long reportId = (Long) obj[0];
                    Long campaignId = (Long) obj[1];
                    String campaignTitle = (String) obj[2];
                    String reason = (String) obj[3];
                    Long reportedBy = (Long) obj[4];
                    LocalDateTime reportedDate = (LocalDateTime) obj[5];
                    String status = (String) obj[6];
                    Long creatorId = (Long) obj[7]; // ✅ 추가된 creatorId

                    // ✅ JSON으로 변환할 수 있도록 Map으로 변환
                    Map<String, Object> response = new HashMap<>();
                    response.put("reportId", reportId);
                    response.put("campaignId", campaignId);
                    response.put("title", campaignTitle);
                    response.put("reason", reason);
                    response.put("reportedBy", reportedBy);
                    response.put("reportedDate", reportedDate);
                    response.put("status", status);
                    response.put("creatorId", creatorId); // ✅ creatorId 추가

                    return response;
                })
                .collect(Collectors.toList());
    }


    //캠페인 신고 처리
    @Transactional
    public void processReport(Long reportId, String action) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("해당 신고를 찾을 수 없습니다."));

        // ✅ 허용된 상태 값으로 변환
        if ("ok".equals(action)) {
            report.setStatus("ok"); // ✅ "게시 정지"는 "ok"로 변경
        } else if ("rejected".equals(action)) {
            report.setStatus("rejected"); // ✅ "보류"는 "rejected"로 변경
        } else {
            throw new IllegalArgumentException("잘못된 액션 값입니다.");
        }

        reportRepository.save(report); // ✅ 변경사항 저장
    }


    //유저 신고 처리
    @Transactional
    public void processUserReport(Long reportId, String action, String reason) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("해당 신고가 존재하지 않습니다."));
        UserEntity user = userRepository.findById(report.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));


        if ("rejected".equals(action)) {
            report.setStatus("rejected");
            user.setStatusReason(reason);
        } else {
            int daysToBan = Integer.parseInt(action);

            user.setUserStatus(0);

            if (daysToBan == 100) {
                user.setUpdatedAt(LocalDateTime.now().plusYears(100));
            } else {
                user.setUpdatedAt(LocalDateTime.now().plusDays(daysToBan));
            }

            user.setStatusReason(reason);
            report.setStatus("ok");
        }


        reportRepository.save(report);
        userRepository.save(user);
    }


    // ✅ 유저 정지 처리 (기간과 사유를 UserEntity에 저장)
    @Transactional
    public void suspendUser(Long userId, int banDays, String reason) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다. ID: " + userId));

        user.setUserStatus(0); // ✅ 정지 상태로 변경
        user.setUpdatedAt(LocalDateTime.now().plusDays(banDays)); // ✅ 정지 해제 예정 날짜 저장
        user.setStatusReason(reason); // ✅ 정지 사유 저장
        userRepository.save(user);
    }

    // ✅ 정지 해제 처리 (새벽 4시에 실행)
    @Transactional
    public int unbanExpiredUsers() {
        List<UserEntity> bannedUsers = userRepository.findAllByUserStatus(0);
        LocalDateTime now = LocalDateTime.now();
        int unbannedCount = 0;

        for (UserEntity user : bannedUsers) {
            if (user.getUpdatedAt() != null && user.getUpdatedAt().isBefore(now)) {
                user.setUserStatus(1); // ✅ 정지 해제
                user.setUpdatedAt(null); // ✅ 날짜 초기화
                user.setStatusReason(null); // ✅ 정지 사유 초기화
                userRepository.save(user);
                unbannedCount++;
                System.out.println("✅ 정지 해제됨 - User ID: " + user.getUserId());
            }
        }
        return unbannedCount;
    }

    // ✅ 새벽 4시 자동 실행 (별도 파일 X)
    @Scheduled(cron = "0 0 4 * * ?")
    public void autoUnbanUsers() {
        int unbannedCount = unbanExpiredUsers();
        System.out.println("🚀 자동 정지 해제 완료: " + unbannedCount + "명 해제됨");
    }
}
