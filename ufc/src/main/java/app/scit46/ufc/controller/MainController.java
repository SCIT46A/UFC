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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

  private final UserService userService;

  // 메인페이지 라우터
  @GetMapping("/")
  public String index(HttpServletRequest request, Model model, RedirectAttributes rttr) {
    HttpSession session = request.getSession(false); // 세션 가져오기
    Long loginUserId = null; // 기본값 설정

    if (session != null) {
      loginUserId = (Long) session.getAttribute("loginUserId"); // 세션이 존재할 때만 값 가져오기
    }

    model.addAttribute("loginUserId", loginUserId);
    rttr.addAttribute("loginUserId", loginUserId);
    //return "index";
    return "redirect:/user/login";
  }

  // 메인페이지에서 검색한 결과를 검색결과 페이지로 보여주기 위한 라우터
  @GetMapping("/search/{type}/{query}")
  public String searchBox(@PathVariable("query") String query, @PathVariable("type") String type, Model model) {
    model.addAttribute("searchText", query);
    model.addAttribute("searchType", type); // ✅ 검색 유형 추가
    return "campaign/all-campaign"; // 검색어를 포함한 뷰 반환
  }

  // 업다 소개페이지 라우터
  @GetMapping("/info")
  public String info() {
    return "info";
  }

}
