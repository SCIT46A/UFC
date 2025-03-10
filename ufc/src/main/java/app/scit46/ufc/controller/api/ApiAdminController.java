package app.scit46.ufc.controller.api;

import java.util.*;
import java.util.stream.Collectors;


import app.scit46.ufc.dto.*;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.campaign.CampaignGoalDTO;


import app.scit46.ufc.entity.CreatorEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


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


    public ApiAdminController(NoticeService noticeService, ReportService reportService, CampaignService campaignService, CreatorService creatorService) {
        this.noticeService = noticeService;
        this.reportService = reportService;
        this.campaignService = campaignService;
        this.creatorService = creatorService;
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
    @PostMapping("/rejected-reasons")
    public ResponseEntity<?> saveRejectedReason(@RequestBody Map<String, String> request) {
        try {
            if (!request.containsKey("campaignId") || !request.containsKey("reason")) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "필수 값이 누락되었습니다."));
            }

            Long campaignId;
            try {
                campaignId = Long.parseLong(request.get("campaignId"));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "잘못된 캠페인 ID 형식"));
            }

            String reason = request.get("reason").trim();

            if (reason.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "거부 사유를 입력해야 합니다."));
            }

            campaignService.saveRejectedReason(campaignId, reason);

            System.out.println("✅ API 성공: 캠페인 " + campaignId + " 거부됨, 사유: " + reason);
            return ResponseEntity.ok(Map.of("success", true, "message", "거부 사유가 저장되었습니다."));
        } catch (Exception e) {
            System.out.println("❌ API 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "거부 사유 저장 중 오류 발생"));
        }
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

    // ✅ 관리자가 직접 정지 해제 실행
    @PostMapping("/user-unban")
    public ResponseEntity<?> unbanUsersManually() {
        int unbannedCount = reportService.unbanExpiredUsers();
        return ResponseEntity.ok(Map.of("success", true, "message", unbannedCount + "명 정지 해제됨"));
    }

    // ✅ 특정 유저 정지
    @PostMapping("/user-ban")
    public ResponseEntity<?> banUser(@RequestParam Long userId, @RequestParam int days, @RequestParam String reason) {
        reportService.suspendUser(userId, days, reason);
        return ResponseEntity.ok(Map.of("success", true, "message", "유저 정지 완료: " + days + "일"));
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
        CreatorEntity creator = creatorService.getCreatorById(creatorId);
        if (creator == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "🚨 창작자를 찾을 수 없습니다."));
        }

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

    //창작자 승인
    @PutMapping("/{creatorId}/approve")
    public ResponseEntity<Map<String, Object>> approveCreator(@PathVariable Long creatorId) {
        try {

            CreatorEntity creator = creatorService.getCreatorById(creatorId);
            if (creator == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "🚨 창작자를 찾을 수 없습니다."));
            }

            creator.setCreatorStatus(true);
            creatorService.saveCreator(creator);

            return ResponseEntity.ok(Map.of("message", "✅ 승인 완료", "creatorId", creatorId));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "🚨 승인 처리 중 오류 발생: " + e.getMessage()));
        }
    }

    //창작자 여러명 승인
    // ✅ 여러 창작자 승인
    @PatchMapping("/creators-approve")
    public ResponseEntity<?> approveMultipleCreators(@RequestBody Map<String, List<Long>> requestBody) {
        List<Long> creatorIds = requestBody.get("creatorIds");

        if (creatorIds == null || creatorIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "창작자 ID 목록이 비어있습니다."));
        }

        int updatedCount = creatorService.approveMultipleCreators(creatorIds);
        return ResponseEntity.ok(Collections.singletonMap("message", updatedCount + "명의 창작자가 승인되었습니다."));
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
    @GetMapping("/creator-status")
    public ResponseEntity<List<Map<String, Object>>> getCreatorStatus() {
        List<CreatorDTO> creators = creatorService.getAllCreators();

        // ✅ JSON 응답을 Map 형태로 변환
        List<Map<String, Object>> responseList = creators.stream().map(creator -> {
            Map<String, Object> creatorMap = new HashMap<>();
            creatorMap.put("creatorId", creator.getCreatorId());
            creatorMap.put("bname", creator.getBName());
            creatorMap.put("companyName", creator.getCompanyName());
            creatorMap.put("creatorStatus", creator.getCreatorStatus());
            return creatorMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

}
