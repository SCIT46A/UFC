package app.scit46.ufc.controller.api;

import java.util.*;


import app.scit46.ufc.dto.*;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.campaign.CampaignGoalDTO;


import app.scit46.ufc.entity.CreatorEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import app.scit46.ufc.service.CreatorService;
import app.scit46.ufc.service.NoticeService;
import app.scit46.ufc.service.ReportService;
import app.scit46.ufc.service.campaign.CampaignService;


@RestController
@RequestMapping("/api/admin")
public class ApiAdminController {

    private final NoticeService noticeService;
    private final ReportService reportService;
    private final CampaignService campaignService;
    private final CreatorService creatorService;
    private final ReportService.UserUnbanScheduler userUnbanScheduler;


    public ApiAdminController(NoticeService noticeService, ReportService reportService, CampaignService campaignService, CreatorService creatorService, ReportService.UserUnbanScheduler userUnbanScheduler) {
        this.noticeService = noticeService;
        this.reportService = reportService;
        this.campaignService = campaignService;
        this.creatorService = creatorService;
        this.userUnbanScheduler = userUnbanScheduler;
    }

    // 캠페인 전체 조회
    @GetMapping("/campaign-status")
    public ResponseEntity<List<CampaignDTO>> getCampaignStatus() {
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    // 사진 빼고 캠페인 조회
    @GetMapping("/campaigns/no-photo")
    public ResponseEntity<List<CampaignDTO>> getCampaignsWithoutPhoto() {
        return ResponseEntity.ok(campaignService.getAllCampaignsWithoutPhoto());
    }

    // 캠페인 목표 조회 API (수량 목표 포함)
    @GetMapping("/campaign-goals")
    public ResponseEntity<List<CampaignGoalDTO>> getAllCampaignGoals() {
        try {
            return ResponseEntity.ok(campaignService.getAllCampaignGoals());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // 캠페인 기부 내역 조회 API
    @GetMapping("/material-donations")
    public ResponseEntity<List<MaterialDonationDTO>> getAllMaterialDonations() {
        try {
            return ResponseEntity.ok(campaignService.getAllMaterialDonations());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // 펀딩 대기 캠페인 목록 조회 API 추가
    @GetMapping("/campaigns-funding-waiting")
    public ResponseEntity<List<CampaignDTO>> getFundingWaitingCampaigns() {
        return ResponseEntity.ok(campaignService.getFundingWaitingCampaigns());
    }


    // 승인 대기 중인 캠페인 목록 조회 API
    @GetMapping("/campaigns-pending")
    public ResponseEntity<List<CampaignDTO>> getPendingCampaigns() {
        return ResponseEntity.ok(campaignService.getPendingCampaigns());
    }

    //하나만 승인
    @PutMapping("/campaigns/{campaignId}/approve")
    public ResponseEntity<Map<String, String>> approveCampaign(@PathVariable Long campaignId) {
        campaignService.approveCampaign(campaignId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "캠페인이 승인되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 여러개 승인
    @PatchMapping("/campaigns-approve")
    public ResponseEntity<Map<String, String>> approveMultipleCampaigns(@RequestBody Map<String, List<Long>> request) {
        List<Long> campaignIds = request.get("campaignIds");
        campaignService.approveMultipleCampaigns(campaignIds);
        Map<String, String> response = new HashMap<>();
        response.put("message", "승인처리 되었습니다.");
        return ResponseEntity.ok(response);
    }


    // 캠페인 신고 현황
    @GetMapping("/campaign-report")
    public ResponseEntity<List<Map<String, Object>>> getReportedCampaigns() {
        return ResponseEntity.ok(reportService.getReportedCampaigns());
    }

    // 캠페인 신고 처리
    @PostMapping("/campaign-reports/action")
    public ResponseEntity<Map<String, Object>> processCampaignReport(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long reportId = Long.parseLong(request.get("reportId"));
            String action = request.get("action");

            reportService.processReport(reportId, action);

            response.put("success", true);
            response.put("message", "신고 처리가 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //유저 신고 목록 API
    @GetMapping("/user-reports")
    public ResponseEntity<List<ReportDTO>> getUserReports() {
        return ResponseEntity.ok(reportService.getAllReportedUsers());
    }

    //유저 신고 조치 API
    @PostMapping("/user-reports/action")
    public ResponseEntity<Map<String, Object>> processUserReport(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long reportId = Long.parseLong(request.get("reportId"));
            String action = request.get("action");
            String reason = request.get("reason");

            System.out.println("🚀 신고 처리 요청: reportId=" + reportId + ", action=" + action + ", reason=" + reason);

            reportService.processUserReport(reportId, action, reason);

            response.put("success", true);
            response.put("message", "신고 처리가 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //정지 해제 즉시 실행
    @PostMapping("/unban-check")
    public ResponseEntity<String> unbanCheck() {
        userUnbanScheduler.unbanExpiredUsers();
        return ResponseEntity.ok("정지 해제 체크 실행 완료");
    }


    //공지사항 목록 조회
    @GetMapping("/notices")
    public ResponseEntity<List<NoticeDTO>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }

    //공지사항 등록
    @PostMapping("/notices/create")
    public ResponseEntity<NoticeDTO> createNotice(@RequestBody NoticeDTO noticeDTO) {
        return ResponseEntity.ok(noticeService.createNotice(noticeDTO));
    }

    // 창작자 사업자 등록 검증 API 엔드포인트
    @PostMapping("/verify/{creatorId}")
    public ResponseEntity<Map<String, Object>> verifyCreator(@PathVariable Long creatorId) {
        // 🔹 DB에서 창작자 정보 가져오기
        CreatorEntity creator = creatorService.getCreatorById(creatorId);
        if (creator == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "🚨 창작자를 찾을 수 없습니다."));
        }

        // 🔹 DTO 생성
        CreatorApprovalDTO dto = CreatorApprovalDTO.builder()
                .bRegistNumber(creator.getBRegistNumber())
                .bName(creator.getBName())
                .startDt("20231128")  // 개업일자 기본값 설정
                .build();

        ResponseEntity<String> response = creatorService.callBusinessValidationAPI(dto);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseData = objectMapper.readValue(response.getBody(), Map.class);

            return ResponseEntity.ok(responseData); // JSON 그대로 반환

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "🚨 검증 결과 처리 중 오류 발생"));
        }
    }



    @PutMapping("/{creatorId}/approve")
    public ResponseEntity<Map<String, Object>> approveCreator(@PathVariable Long creatorId) {
        try {
            // 🔹 1. 해당 창작자 정보 가져오기
            CreatorEntity creator = creatorService.getCreatorById(creatorId);
            if (creator == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "🚨 창작자를 찾을 수 없습니다."));
            }

            // 🔹 2. 승인 처리 (creatorStatus = true 로 변경)
            creator.setCreatorStatus(true);
            creatorService.saveCreator(creator);

            // 🔹 3. JSON 응답 반환 (프론트엔드에서 JSON 파싱할 수 있도록)
            return ResponseEntity.ok(Map.of("message", "✅ 승인 완료", "creatorId", creatorId));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "🚨 승인 처리 중 오류 발생: " + e.getMessage()));
        }
    }


    // ✅ 3. 창작자 승인 대기 목록 조회 API
    @GetMapping("/creator-approval")
    public ResponseEntity<List<CreatorDTO>> getPendingCreators() {
        try {
            List<CreatorDTO> pendingCreators = creatorService.getPendingCreators();
            return ResponseEntity.ok(pendingCreators);
        } catch (Exception e) {
            System.err.println("🚨 창작자 승인 목록 조회 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

}
