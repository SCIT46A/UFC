package app.scit46.ufc.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Role 생성
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ADMIN", "USER", "CREATOR");

        // 기본 userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 네이버 로그인인 경우, attributes 안에 response를 펼쳐서 사용
        if ("naver".equals(registrationId)) {
            Map<String, Object> responseAttributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            if (responseAttributes == null) {
                throw new OAuth2AuthenticationException("네이버 로그인 응답에서 response가 누락되었습니다.");
            }
            // 여기서 responseAttributes는 네이버의 사용자 정보를 포함하는 맵이며, "id" 키가 포함됨
            // 따라서 userNameAttributeName을 "id"로 지정합니다.
            return new DefaultOAuth2User(authorities, responseAttributes, "id");
        }

        // 네이버가 아닌 경우는 그대로 반환
        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), userNameAttributeName);
    }



}