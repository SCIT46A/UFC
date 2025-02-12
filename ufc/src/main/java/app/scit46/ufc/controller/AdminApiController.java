package app.scit46.ufc.controller;

import app.scit46.ufc.dto.NoticeDTO;
import app.scit46.ufc.dto.CampaignDTO;
import app.scit46.ufc.dto.ReportDTO;
import app.scit46.ufc.service.NoticeService;
import app.scit46.ufc.service.CampaignService;
import app.scit46.ufc.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AdminApiController {

    private final NoticeService noticeService;
    private final ReportService reportService;
    private final CampaignService campaignService;

    public AdminApiController(NoticeService noticeService, ReportService reportService, CampaignService campaignService) {
        this.noticeService = noticeService;
        this.reportService = reportService;
        this.campaignService = campaignService;
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

    // ✅ 공지사항 생성 폼 로드 API
    @Controller
    @RequestMapping("/admin")
    public static class AdminPageController {
        @GetMapping("/notice-form")
        public String showNoticeForm() {
            return "admin/notice-form";
        }
    }
}
