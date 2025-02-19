package app.scit46.ufc.config;


import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class OauthLogin implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = defaultOAuth2User.getAttributes();


        String identity = null;  // Google: sub, Kakao: id
        String nickname = null;  // Google: name, Kakao: properties.nickname
        String email = null;     // Google: email, Kakao: kakao_account.email
        String provider = null;
        if (attributes.containsKey("sub")) {
            // Google 사용자 정보 추출
            identity = (String) attributes.get("sub");
            nickname = (String) attributes.get("name");
            email = (String) attributes.get("email");
            provider = "google";
        } else if (attributes.containsKey("id")) {
            // Kakao 사용자 정보 추출
            identity = attributes.get("id").toString();
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            provider = "kakao";
            nickname = properties != null ? (String) properties.get("nickname") : null;
            email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        } else if (attributes.containsKey("response")) {
            // 네이버 사용자 정보 추출
            Map<String, Object> responseAttributes = (Map<String, Object>) attributes.get("response");
            identity = (String) responseAttributes.get("id");
            nickname = (String) responseAttributes.get("name");
            email = (String) responseAttributes.get("email");
            provider = "naver";
        }


        // 사용자 정보 처리
        UserEntity existingUser = userService.findUserByIdentity(identity);

        if (existingUser != null) {
            // 이미 존재하는 회원 -> 세션에 정보 저장 후 루트 페이지로 이동
            request.getSession().setAttribute("loginUserId", existingUser.getUserId());

            //관리자 여부 확인
            boolean isAdmin = "ADMIN".equals(existingUser.getRoles());


            // 관리자면 /admin, 일반 유저면 /
            if (isAdmin) {
                response.sendRedirect("/admin/adminPage");  // 관리자일 경우 `/admin`으로 이동
            } else {
                response.sendRedirect("/");  // 일반 유저는 `/`으로 이동
            }

        } else {
            // 신규 회원 -> 세션에 제공자 정보 저장 후 가입 페이지로 이동
            request.getSession().setAttribute("find", provider);
            request.getSession().setAttribute("identity", identity);
            request.getSession().setAttribute("nickname", nickname);
            request.getSession().setAttribute("email", email);
            response.sendRedirect("/user/join");
        }
    }

    private String getProvider(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        return ((DefaultOAuth2User) oAuth2User).getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> {
                    String authority = grantedAuthority.getAuthority();
                    if (authority.contains("google")) return "google";
                    if (authority.contains("kakao")) return "kakao";
                    if (authority.contains("naver")) return "naver";
                    return "unknown";
                })
                .orElse("unknown");
    }
}
