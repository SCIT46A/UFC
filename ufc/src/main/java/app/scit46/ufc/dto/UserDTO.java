package app.scit46.ufc.dto;

import java.time.LocalDateTime;

import app.scit46.ufc.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private ImageUrlDTO photo; // ✅ PhotoDTO 포함
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
                .photo(entity.getPhotoId() != null ? ImageUrlDTO.toDTO(entity.getPhotoId()) : null) // ✅ PhotoDTO 변환
                .intro(entity.getIntro())
                .isMarketed(entity.getIsMarketed())
                .userStatus(entity.getUserStatus())
                .statusReason(entity.getStatusReason())
                .build();
    }
}
