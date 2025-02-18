package app.scit46.ufc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    @Autowired
    private AuthenticationSuccessHandler oauthSuccessHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                            .csrf().disable() // CSRF 보호 비활성화 (필요에 따라)
                            .authorizeRequests()
                            .anyRequest().permitAll(); // 모든 요청 허용
        /*
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().permitAll()
                
                        //.requestMatchers("/**").permitAll()
                        //.anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable());
                
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(oauthSuccessHandler)
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
        */
        return http.build();
    }
}




