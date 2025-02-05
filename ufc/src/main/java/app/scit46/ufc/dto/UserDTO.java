package app.scit46.ufc.dto;

import java.time.LocalDateTime;

import app.scit46.ufc.entity.PrivatePhotoEntity;
import app.scit46.ufc.entity.UserEntity;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserDTO {
    private Long userId;
    private String oauthId;
    private String loginType;
    private String userName;
    private String email;
    private String phoneNumber;
    private String userAddress;
    private String roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PrivatePhotoEntity photoId;
    private String intro;
    private int isMarketed;
    private int userStatus;
    private int statusReason;
    
    public static UserDTO toDTO(UserEntity userEntity) {
        return UserDTO.builder()
                .userId(userEntity.getUserId())
                .oauthId(userEntity.getOauthId())
                .loginType(userEntity.getLoginType())
                .userName(userEntity.getUserName())
                .email(userEntity.getEmail())
                .phoneNumber(userEntity.getPhoneNumber())
                .userAddress(userEntity.getUserAddress())
                .roles(userEntity.getRoles())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .photoId(userEntity.getPhotoId())
                .intro(userEntity.getIntro())
                .isMarketed(userEntity.getIsMarketed())
                .userStatus(userEntity.getUserStatus())
            .build();
    }
}
