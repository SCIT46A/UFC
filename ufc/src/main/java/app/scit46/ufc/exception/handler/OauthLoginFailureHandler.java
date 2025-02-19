package app.scit46.ufc.exception.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
public class OauthLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        log.warn("⚠️ OAuth 로그인 실패 발생!");
        if (session != null) {
            log.warn("✅ 기존 세션 존재 → 세션 초기화 진행");
            session.invalidate(); // ✅ 로그인 실패 시 세션 초기화
        } else {
            log.warn("❌ 세션이 존재하지 않음!");
        }

        // 실패 원인 로그 출력
        log.error("❌ OAuth 로그인 실패 원인: {}", exception.getMessage());

        // ✅ JSESSIONID 쿠키 삭제
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        response.sendRedirect("/login?error"); // 로그인 실패 후 에러 페이지로 이동
    }

}