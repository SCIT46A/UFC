package app.scit46.ufc.dto;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import app.scit46.ufc.entity.PrivatePhotoEntity;
import app.scit46.ufc.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class LoginUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;
    
    private String username;
    private PrivatePhotoEntity photoid;
    private String intro;
    private String phonenumber;
    private String useraddress;

    public String getUserAddress() {
        return this.useraddress;
    }

    public String getIntro() {
        return this.intro;
    }

    public String getPhoneNumber() {
        return this.phonenumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return this.username;
    }
    
    public PrivatePhotoEntity getPhotoId;

    public static LoginUserDetails toDTO(UserEntity entity) {
        return LoginUserDetails.builder()
                .username(entity.getUserName())
                .photoid(entity.getPhotoId())
                .intro(entity.getIntro())
                .useraddress(entity.getUserAddress())
                .phonenumber(entity.getPhoneNumber())
                .build();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

} 