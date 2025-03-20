package app.scit46.ufc.config;


import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OauthLogin implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = defaultOAuth2User.getAttributes();

        String identity = null;  // Google: sub, Kakao: id, Naver: id
        String nickname = null;  // Google: name, Kakao: properties.nickname, Naver: name
        String email = null;     // Google: email, Kakao: kakao_account.email, Naver: email
        String provider = null;

        if (attributes.containsKey("sub")) {
            // Google 사용자 정보 추출
            identity = (String) attributes.get("sub");
            nickname = (String) attributes.get("name");
            email = (String) attributes.get("email");
            provider = "google";
        } else if (attributes.containsKey("id")) {
            // 카카오와 네이버 둘 다 "id"가 있으므로 카카오의 경우 추가 키가 있음
            if (attributes.containsKey("kakao_account")) {
                // Kakao 사용자 정보 추출
                identity = attributes.get("id").toString();
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
                provider = "kakao";
                nickname = properties != null ? (String) properties.get("nickname") : null;
                email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            } else {
                // 네이버 로그인 (평탄화된 경우)
                identity = (String) attributes.get("id");
                nickname = (String) attributes.get("name");
                email = (String) attributes.get("email");
                provider = "naver";
            }
        }
        // 기존에 attributes.containsKey("response")인 경우는 더 이상 발생하지 않음

        // 이하 로그인 처리 로직은 그대로 진행
        UserEntity existingUser = userService.findUserByIdentity(identity);
        if (existingUser != null) {
            request.getSession().setAttribute("loginUserId", existingUser.getUserId());
            String redirectUrl = request.getParameter("redirectUrl");
            if (redirectUrl == null || redirectUrl.isEmpty()) {
                redirectUrl = (String) request.getSession().getAttribute("redirectUrl");
            } else {
                request.getSession().setAttribute("redirectUrl", redirectUrl);
            }
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                request.getSession().removeAttribute("redirectUrl");
                response.sendRedirect(redirectUrl);
                return;
            }
            boolean isAdmin = "ADMIN".equals(existingUser.getRoles());
            if (isAdmin) {
                response.sendRedirect("/admin/adminPage");
            } else {
                response.sendRedirect("/");
            }
        } else {
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
