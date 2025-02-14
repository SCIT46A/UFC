package app.scit46.ufc.controller;

import app.scit46.ufc.dto.*;
import app.scit46.ufc.service.NoticeService;
import app.scit46.ufc.service.CampaignService;
import app.scit46.ufc.service.ReportService;
import app.scit46.ufc.service.CreatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AdminApiController {

    private final NoticeService noticeService;
    private final ReportService reportService;
    private final CampaignService campaignService;
    private final CreatorService creatorService;

    public AdminApiController(NoticeService noticeService, ReportService reportService, CampaignService campaignService, CreatorService creatorService) {
        this.noticeService = noticeService;
        this.reportService = reportService;
        this.campaignService = campaignService;
        this.creatorService = creatorService;
    }

    // ✅ 공지사항 목록 조회 API
    @GetMapping("/notices")
    public ResponseEntity<List<NoticeDTO>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }

    // ✅ 공지사항 등록 API
    @PostMapping("/notices/create")
    public ResponseEntity<NoticeDTO> createNotice(@RequestBody NoticeDTO noticeDTO) {
        return ResponseEntity.ok(noticeService.createNotice(noticeDTO));
    }

    // ✅ 유저 신고 목록 API
    @GetMapping("/user-reports")
    public ResponseEntity<List<ReportDTO>> getUserReports() {
        return ResponseEntity.ok(reportService.getAllReportedUsers());
    }

    // ✅ 유저 신고 조치 API
    @PostMapping("/user-reports/action")
    public ResponseEntity<Map<String, Object>> processUserReport(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long reportId = Long.parseLong(request.get("reportId"));
            String action = request.get("action");

            reportService.processUserReport(reportId, action, null); // ✅ `banEndDate` 삭제

            response.put("success", true);
            response.put("message", "신고 처리가 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // ✅ 캠페인 운영 현황 API (올바른 엔드포인트 유지)
    @GetMapping("/campaign-status")
    public ResponseEntity<List<CampaignDTO>> getCampaignStatus() {
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    // ✅ 캠페인 목표 조회 API (수량 목표 포함)
    @GetMapping("/campaign-goals")
    public ResponseEntity<List<CampaignGoalDTO>> getAllCampaignGoals() {
        try {
            return ResponseEntity.ok(campaignService.getAllCampaignGoals());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // ✅ 캠페인 기부 내역 조회 API
    @GetMapping("/material-donations")
    public ResponseEntity<List<MaterialDonationDTO>> getAllMaterialDonations() {
        try {
            return ResponseEntity.ok(campaignService.getAllMaterialDonations());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // ✅ 캠페인 신고 현황 API (조회만 가능)
    @GetMapping("/campaign-report")
    public ResponseEntity<List<ReportDTO>> getReportedCampaigns() {
        return ResponseEntity.ok(reportService.getReportedCampaigns());
    }

    // ✅ 승인 대기 중인 캠페인 목록 조회 API
    @GetMapping("/campaigns-pending")
    public ResponseEntity<List<CampaignDTO>> getPendingCampaigns() {
        return ResponseEntity.ok(campaignService.getPendingCampaigns());
    }

    // ✅ **캠페인 승인 API** (URL 통일)
    @PutMapping("/campaigns/{campaignId}/approve")
    public ResponseEntity<String> approveCampaign(@PathVariable Long campaignId) {
        try {
            campaignService.approveCampaign(campaignId);
            return ResponseEntity.ok("캠페인이 승인되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("캠페인 승인 중 오류 발생: " + e.getMessage());
        }
    }

    // ✅ 창작자 승인 대기 목록 조회 API
    @GetMapping("/creator-approval")
    public ResponseEntity<List<Map<String, Object>>> getPendingCreators() {
        List<CreatorDTO> creators = creatorService.getPendingCreators();

        List<Map<String, Object>> formattedCreators = creators.stream()
                .map(creator -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("creatorId", creator.getCreatorId());
                    map.put("companyName", creator.getCompanyName());
                    map.put("bName", creator.getBName());
                    map.put("bRegistNumber", creator.getBRegistNumber());
                    map.put("address", creator.getAddress());
                    map.put("creatorStatus", creator.getCreatorStatus());

                    if (creator.getOwnUser() != null) {
                        map.put("userId", creator.getOwnUser().getUserId());
                        map.put("userName", creator.getOwnUser().getUserName());
                        map.put("email", creator.getOwnUser().getEmail());
                        map.put("createdAt", creator.getOwnUser().getCreatedAt() != null ? creator.getOwnUser().getCreatedAt().toString() : null);
                        map.put("updatedAt", creator.getOwnUser().getUpdatedAt() != null ? creator.getOwnUser().getUpdatedAt().toString() : null);
                    }
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(formattedCreators);
    }

    // ✅ 창작자 승인 API (URL 통일)
    @PostMapping("/creators/{creatorId}/approve")
    public ResponseEntity<String> approveCreator(@PathVariable Long creatorId) {
        try {
            creatorService.approveCreator(creatorId);
            return ResponseEntity.ok("창작자 승인 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("창작자 승인 중 오류 발생: " + e.getMessage());
        }
    }
}
