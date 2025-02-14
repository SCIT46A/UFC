package app.scit46.ufc.dto;

import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.PrivatePhotoEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

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
    private String statusReason;

    public static UserDTO toDTO(UserEntity entity) {
        return UserDTO.builder()
                .userId(entity.getUserId())
                .oauthId(entity.getOauthId())
                .loginType(entity.getLoginType())
                .userName(entity.getUserName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .userAddress(entity.getUserAddress())
                .roles(entity.getRoles())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .photoId(entity.getPhotoId())
                .intro(entity.getIntro())
                .isMarketed(entity.getIsMarketed())
                .userStatus(entity.getUserStatus())
                .statusReason(entity.getStatusReason())
                .build();
    }
}
