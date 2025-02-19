package app.scit46.ufc.exception.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class OAuthSessionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            if (session.getAttribute(
                    "org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository.AUTHORIZATION_REQUEST"
            ) != null && request.getRequestURI().equals("/")) {
                // ✅ 로그인 페이지로 접근하는 경우에만 세션 삭제
                log.warn("⚠️ OAuth 로그인 중단 감지 - 세션 초기화");
                session.invalidate();
            }

        }


        filterChain.doFilter(request, response);
    }
}
