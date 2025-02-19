package app.scit46.ufc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import app.scit46.ufc.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

  private final UserService userService;

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
    return "info";
  }

}
