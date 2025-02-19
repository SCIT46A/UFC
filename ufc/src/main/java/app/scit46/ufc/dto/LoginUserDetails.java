package app.scit46.ufc.dto;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import app.scit46.ufc.entity.PrivatePhotoEntity;
import app.scit46.ufc.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;
    
    private String userName;
    private PrivatePhotoEntity photoId;
    private String intro;
    private String phoneNumber;
    private String userAddress;


    public static LoginUserDetails fromEntity(UserEntity userEntity) {
        return LoginUserDetails.builder()
        .userName(userEntity.getUserName())
        .photoId(userEntity.getPhotoId())
        .intro(userEntity.getIntro())
        .phoneNumber(userEntity.getPhoneNumber())
        .userAddress(userEntity.getUserAddress())
                .build();
    }
    public String getUserAddress() {
        return this.userAddress;
    }

    public String getIntro() {
        return this.intro;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return this.userName;
    }
    
    // public PrivatePhotoEntity getPhotoId;

    // public static LoginUserDetails toDTO(UserEntity entity) {
    //     return LoginUserDetails.builder()
    //             .username(entity.getUserName())
    //             .photoid(entity.getPhotoId())
    //             .intro(entity.getIntro())
    //             .useraddress(entity.getUserAddress())
    //             .phonenumber(entity.getPhoneNumber())
    //             .build();
    // }

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