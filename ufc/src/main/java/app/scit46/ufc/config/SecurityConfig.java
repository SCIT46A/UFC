package app.scit46.ufc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

        // @Bean
        // @Primary
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http.authorizeHttpRequests(auth -> auth
                                .requestMatchers("/**").permitAll()
                                .anyRequest().authenticated())
                                .csrf(csrf -> csrf.disable())
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

                http.addFilterBefore(oAuthSessionFilter, LogoutFilter.class);
                return http.build();
        }

}
