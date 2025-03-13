package app.scit46.ufc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import app.scit46.ufc.exception.handler.OAuthSessionFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
    private final OauthLogin oauthSuccessHandler;
    private final OAuthSessionFilter oAuthSessionFilter;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated())
                // CSRF는 이미 disable되어 있지만, 혹시 필요한 경우 CORS도 활성화
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(oauthSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService)))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        // WebSocket 관련 요청이 필터 체인을 타지 않도록 설정
        http.addFilterBefore(oAuthSessionFilter, LogoutFilter.class);
        return http.build();
    }

    // /ws/** 경로를 스프링 시큐리티 필터에서 제외하여 SockJS fallback 요청도 허용합니다.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/ws/**");
    }

}
