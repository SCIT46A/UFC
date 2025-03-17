package app.scit46.ufc.controller;

import java.util.List;

import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.cloudflare.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import app.scit46.ufc.dto.ReportDTO;
import app.scit46.ufc.service.ReportService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ReportService reportService;
    private final UserService userService;
    private final ImageService imageService;

    // 관리자 페이지 전체 (`admin-user.html` 반환)
    @GetMapping("/adminPage")
    public String adminPage(HttpServletRequest request, Model model) {
        UserDTO user = UserDTO.toDTO(userService.findUserByIdentity(request.getUserPrincipal().getName()));
        String imageUrl = imageService.getImageUrl(user.getPhoto().getImageId());

        model.addAttribute("userId", user.getUserName());
        model.addAttribute("userImage",imageUrl);
        return "admin/admin-user";
    }

    
    @GetMapping("/notice-form")
    public String showNoticeForm() {
        return "admin/notice-form";
    }

    // 신고된 유저 목록 페이지 (AJAX 요청 처리)
    @GetMapping("/reports/user-report")
    public String getReportedUsers(Model model) {
        List<ReportDTO> reportedUsers = reportService.getAllReportedUsers();
        model.addAttribute("reportedUsers", reportedUsers);
        return "admin/admin-user-report :: content";  // `content` fragment만 반환
    }

}
