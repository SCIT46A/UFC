package app.scit46.ufc.service;

import app.scit46.ufc.dto.ReportDTO;
import app.scit46.ufc.entity.ReportEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.ReportRepository;
import app.scit46.ufc.repository.UserRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
                    Long reportId = (Long) obj[0];
                    Long campaignId = (Long) obj[1];
                    String campaignTitle = (String) obj[2];
                    String reason = (String) obj[3];
                    Long reportedBy = (Long) obj[4];
                    LocalDateTime reportedDate = (LocalDateTime) obj[5];
                    String status = (String) obj[6];

                    // ✅ JSON으로 변환할 수 있도록 Map으로 변환
                    Map<String, Object> response = new HashMap<>();
                    response.put("reportId", reportId);
                    response.put("campaignId", campaignId);
                    response.put("title", campaignTitle);
                    response.put("reason", reason);
                    response.put("reportedBy", reportedBy);
                    response.put("reportedDate", reportedDate);
                    response.put("status", status);

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


    // ✅ 유저 정지 해제 기능 (매일 00시 실행)
    @Component
    @EnableScheduling // ✅ 스케줄러 활성화 (필수!)
    public class UserUnbanScheduler {

        private final UserRepository userRepository;

        public UserUnbanScheduler(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        // ✅ 유저 정지 해제 기능 (매분 실행 테스트)
        @Scheduled(fixedRate = 60000) // 1분마다 실행
        @Transactional
        public void unbanExpiredUsers() {
            System.out.println("🕒 정지 해제 체크 실행됨: " + LocalDateTime.now()); // ✅ 실행 로그 추가

            List<UserEntity> bannedUsers = userRepository.findAllByUserStatus(0); // ✅ 정지된 유저 찾기

            for (UserEntity user : bannedUsers) {
                if (user.getUpdatedAt() != null && !user.getUpdatedAt().isAfter(LocalDateTime.now())) {
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


}
