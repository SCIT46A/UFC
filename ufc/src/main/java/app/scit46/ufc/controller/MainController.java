package app.scit46.ufc.controller;

import app.scit46.ufc.dto.*;
import app.scit46.ufc.entity.UserAlertEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.repository.UserAlertRepository;
import app.scit46.ufc.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class MainController {

  @Autowired
  private UserService userService;

  @GetMapping("/")
  public String index(HttpServletRequest request, Model model) {
    HttpSession session = request.getSession(false); // 세션 가져오기
    Long loginUserId = null; // 기본값 설정

    if (session != null) {
      loginUserId = (Long) session.getAttribute("loginUserId"); // 세션이 존재할 때만 값 가져오기
    }

    model.addAttribute("loginUserId", loginUserId);
    return "index";
  }

  @GetMapping("/search/{type}/{query}")
  public String searchBox(@PathVariable("query") String query, @PathVariable("type") String type, Model model) {
    model.addAttribute("searchText", query);
    model.addAttribute("searchType", type); // ✅ 검색 유형 추가
    return "campaign/all-campaign"; // 검색어를 포함한 뷰 반환
  }


  @GetMapping("/info")
  public String info() {
    return "upda/info";
  }

}
