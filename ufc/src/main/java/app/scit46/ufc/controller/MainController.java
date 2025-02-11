package app.scit46.ufc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import app.scit46.ufc.dto.TagDTO;
import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.service.TagService;
import app.scit46.ufc.service.UserAlertService;
import app.scit46.ufc.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

  @Autowired
  private UserAlertService userAlertService;

  @Autowired
  private UserService userService;

  @Autowired
  private TagService tagService;

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


// 검색시 사용하는 검색창
  @GetMapping("/campagin/all/{search}")
  public String allCampaign(@PathVariable("search") String search, Model model) {
    model.addAttribute("searchText", search);
    return "campaign/all-campaign"; // 검색어를 포함한 뷰 반환
  }

//  알림상태 확인하는거
  @GetMapping("/check-alert")
  @ResponseBody //
  public Boolean checkAlert(HttpServletRequest request) {
    HttpSession session = request.getSession(false); // 세션 가져오기

    if (session != null) {
      Long loginUserId = (Long) session.getAttribute("loginUserId"); // 세션에서 userId 가져오기
      return userAlertService.alertCheck(loginUserId);
    }

    return false; // 세션이 없거나, 알람이 없음
  }

//  로그인 상태 확인?

  @GetMapping("check-login")
  @ResponseBody
  public UserDTO checkLogin(HttpServletRequest request) {
    HttpSession session = request.getSession(false); // 세션 가져오기
    UserDTO user = null;
    if (session != null) {
      Long loginUserId = (Long) session.getAttribute("loginUserId"); // 세션에서 userId 가져오기
      try {
        user = userService.readUserById(loginUserId);
        return user;
      } catch (DBNotFoundException e) {
        return null;
      }
    }
    return user;
  }

//  카테고리 입력

  @GetMapping("check-tag")
  @ResponseBody
  public List<TagDTO> checkTag() {
    return tagService.getTopTags();
  }


  @GetMapping("logout")
  public String logout(HttpSession session) {
    session.removeAttribute("loginUserId");
    return "redirect:/";
  }




  @GetMapping("/info")
  public String info() {
    return "info";
  }
}
