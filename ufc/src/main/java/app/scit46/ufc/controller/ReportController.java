package app.scit46.ufc.controller;

import app.scit46.ufc.dto.ReportDTO;
import app.scit46.ufc.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/reports")
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 관리자 페이지 전체 (`admin-user.html` 반환)
    @GetMapping
    public String adminUserPage() {
        return "admin/admin-user";
    }

    // 신고된 유저 목록 페이지 (AJAX 요청 처리)
    @GetMapping("/user-report")
    public String getReportedUsers(Model model) {
        List<ReportDTO> reportedUsers = reportService.getAllReportedUsers();
        model.addAttribute("reportedUsers", reportedUsers);
        return "admin/admin-user-report :: content";  // `content` fragment만 반환
    }
}
