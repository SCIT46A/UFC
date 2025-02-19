package app.scit46.ufc.controller;

import app.scit46.ufc.dto.ReportDTO;
import app.scit46.ufc.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/adminPage")
    public String adminPage() {
        return "admin/admin-user";
    }

}
