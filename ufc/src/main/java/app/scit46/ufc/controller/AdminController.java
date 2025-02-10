package app.scit46.ufc.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "/admin/admin-login";  // admin-login.html로 이동
    }

    @GetMapping("/admin/adminPage")
    public String adminPage() {
        return "admin/admin-user";  // templates/admin/admin-user.html 파일을 반환
    }
}
