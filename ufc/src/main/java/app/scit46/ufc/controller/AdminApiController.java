package app.scit46.ufc.controller;

import app.scit46.ufc.dto.NoticeDTO;
import app.scit46.ufc.dto.CampaignDTO;
import app.scit46.ufc.dto.ReportDTO;
import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.service.NoticeService;
import app.scit46.ufc.service.CampaignService;
import app.scit46.ufc.service.ReportService;
import app.scit46.ufc.service.CreatorService;
import com.nimbusds.jose.shaded.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> processUserReport(@RequestBody Map<String, String> request) {
        try {
            Long reportId = Long.parseLong(request.get("reportId"));
            String action = request.get("action");
            String reason = request.get("reason");

            reportService.processUserReport(reportId, action, reason);
            return ResponseEntity.ok("신고 처리 완료!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("신고 처리 중 오류 발생: " + e.getMessage());
        }
    }

    // ✅ 캠페인 운영 현황 API
    @GetMapping("/campaign-status")
    public ResponseEntity<List<CampaignDTO>> getAllCampaigns() {
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    // ✅ 캠페인 신고 현황 API (조회만 가능)
    @GetMapping("/campaign-report")
    public ResponseEntity<List<ReportDTO>> getReportedCampaigns() {
        return ResponseEntity.ok(reportService.getReportedCampaigns());
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




    @PostMapping("/creator-approval/approve")
    public ResponseEntity<String> approveCreator(@RequestBody Map<String, Long> request) {
        try {
            Long creatorId = request.get("creatorId");

            if (creatorId == null) {
                return ResponseEntity.badRequest().body("창작자 ID가 제공되지 않았습니다.");
            }

            System.out.println("✔ Approving Creator ID: " + creatorId); // 디버깅용 로그 추가
            creatorService.approveCreator(creatorId);
            return ResponseEntity.ok("창작자 승인 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("창작자 승인 중 오류 발생: " + e.getMessage());
        }
    }

}
