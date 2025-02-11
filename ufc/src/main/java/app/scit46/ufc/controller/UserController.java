package app.scit46.ufc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping({"/",""})
    public String index() {
        return "user/mypage-profile";
    }

    @GetMapping("/alarm")
    public String alarm() {
        return "user/mypage-alarm";
    }

    @GetMapping("/like")
    public String like() {
        return "user/mypage-like";
    }

    @GetMapping("/reply")
    public String reply() {
        return "user/mypage-reply";
    }

    @GetMapping("/sponsor")
    public String sponsor() {
        return "user/mypage-sponsor";
    }

    @GetMapping("/sponsor/detail")
    public String alarmDetail() {
        return "user/mypage-sponsor-detail";
    }

    @GetMapping("/edit")
    public String edit() {
        return "user/mypage-profile-edit";
    }

    
//    로그인 관련
    
    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    @GetMapping("/join")
    public String joinDetailPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // 세션에서 카카오 정보 꺼내기
            String identity = (String) session.getAttribute("identity");
            String nickname = (String) session.getAttribute("nickname");
            String email = (String) session.getAttribute("email");
            String find = (String) session.getAttribute("find");

            // 모델에 담아 뷰로 전달
            model.addAttribute("identity", identity);
            model.addAttribute("nickname", nickname);
            model.addAttribute("email", email);
            model.addAttribute("find", find);
        }
        return "login/joindetail";
    }

    @PostMapping("/joindetail")
    public String joinDetailSubmit(HttpServletRequest request, @RequestParam("check") int check ,
                                   @RequestParam("address") String address,
                                   @RequestParam("phone") String phone,
                                   @RequestParam("intro") String intro
                                   )
    {
        HttpSession session = request.getSession(false);
        if (session == null) {
            // 세션이 없으면 에러 처리
            return "redirect:/error";
        }
        String kakaoId = (String) session.getAttribute("identity");
        String nickname = (String) session.getAttribute("nickname");
        String email = (String) session.getAttribute("email");
        String find = (String) session.getAttribute("find");


        // DB 저장 (UserDTO 만들거나 직접)
        UserDTO userDTO = new UserDTO();
        userDTO.setOauthId(kakaoId);
        userDTO.setUserName(nickname);
        userDTO.setEmail(email);
        userDTO.setLoginType(find);
        userDTO.setIsMarketed(check);
        userDTO.setUserAddress(address);
        userDTO.setPhoneNumber(phone);
        userDTO.setIntro(intro);

        // userService.add(...) 저장
        UserEntity savedUser = userService.saveUser(userDTO);

        // 가입 완료 후, 세션에 로그인 정보 저장
        session.setAttribute("loginUserId", savedUser.getUserId());

        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }


}

