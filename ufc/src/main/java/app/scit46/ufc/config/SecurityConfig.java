package app.scit46.ufc.config;

import app.scit46.ufc.exception.handler.OAuthSessionFilter;
import app.scit46.ufc.exception.handler.OauthLoginFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private OAuth2UserService oAuth2UserService;

    @Autowired
    private OauthLogin oauthSuccessHandler;

    @Autowired
    private OAuthSessionFilter oAuthSessionFilter;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        //.requestMatchers("/admin/**").hasRole("ADMIN")  // 관리자 페이지는 ADMIN 권한 필요
                        .requestMatchers("/**").permitAll()
//                        .requestMatchers("/favicon.ico").permitAll()
                        .anyRequest().authenticated()
                )

                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(oauthSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        http.addFilterBefore(oAuthSessionFilter, LogoutFilter.class);
        return http.build();
    }

}




