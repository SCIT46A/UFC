package app.scit46.ufc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // 유저 기본페이지 조회
    @GetMapping({"/",""})
    public String index(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false); // 세션 가져오기
        Long loginUserId = null; // 기본값 설정

        if (session != null) {
            loginUserId = (Long) session.getAttribute("loginUserId"); // 세션이 존재할 때만 값 가져오기
            if (loginUserId != null) {
                //사용자 정보를 데이터베이스에서 조회
                UserEntity user = userService.findById(loginUserId);
                model.addAttribute("user", user);
            }
        }
        return "user/mypage-profile";
    }



    // 세션 날리는거 다시한번 체크하기
    @GetMapping("/delete")
    public String delete(
        HttpSession session,
            @RequestParam(name = "userId") Long userId) {
        userService.delete(userId);
        session.removeAttribute("loginUserId");
        return "logout";
    }
    

    @PostMapping("/update")
    public String postMethodName(
            HttpServletRequest request
            , @ModelAttribute UserDTO userDTO
            // ,@RequestParam(name="userName") String userName
            // , @RequestParam(name = "intro") String intro
            // , @RequestParam(name = "phoneNumber") String phoneNumbe
            // , @RequestParam(name = "userAddress") String userAddress
            , RedirectAttributes rttr
    ) {
        userService.userUpdate(userDTO);


        return "redirect:/user";
    }
    
    


    @GetMapping("/badge")
    public String badge() {
        return "user/mypage-badge";
    }

    @GetMapping("/like")
    public String like() {
        return "user/mypage-like";
    }

    @GetMapping("/reply")
    public String reply() {
        return "user/mypage-reply";
    }

    @GetMapping("/donation")
    public String donation() {
        return "user/mypage-donation";
    }

    @GetMapping("/donation/detail")
    public String alarmDetail() {
        return "user/mypage-donation-detail";
    }

    // 유저 정보 수정 창 조회
    @GetMapping("/edit")
    public String edit(HttpServletRequest request, Model model) {
    HttpSession session = request.getSession(false); // 세션 가져오기
    Long loginUserId = null; // 기본값 설정
    if (session != null) {
        loginUserId = (Long) session.getAttribute("loginUserId"); // 세션이 존재할 때만 값 가져오기
        if (loginUserId != null) {
            //사용자 정보를 데이터베이스에서 조회
            UserEntity user = userService.findById(loginUserId);
            model.addAttribute("user", user);
        }
    }
        return "user/mypage-profile-edit";
    }

    
//    로그인 관련
    
    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    @GetMapping("/join")
    public String joinDetailPage(HttpServletRequest request, Model model
    ) {
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
        return "/login/joindetail";
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

