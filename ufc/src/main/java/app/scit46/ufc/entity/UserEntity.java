package app.scit46.ufc.entity;

import java.time.LocalDateTime;

import app.scit46.ufc.dto.UserDTO;
import jakarta.persistence.*;
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
@Entity
@Table(name = "Users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "oauth_id")
    private String oauthId;
    @Column(name = "login_type")
    private String loginType;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "user_address")
    private String userAddress;
    @Column(name = "roles")
    private String roles;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private PrivatePhotoEntity photoId;
    @Column(name = "intro")
    private String intro;
    @Column(name = "is_marketed")
    private int isMarketed;
    @Column(name = "user_status")
    private int userStatus;
    @Column(name = "status_reason")
    private int statusReason;


    public static UserEntity toEntity(UserDTO userDTO, PrivatePhotoEntity photoId) {
        return UserEntity.builder()
            .userId(userDTO.getUserId())
            .oauthId(userDTO.getOauthId())
            .loginType(userDTO.getLoginType())
            .userName(userDTO.getUserName())
            .email(userDTO.getEmail())
            .phoneNumber(userDTO.getPhoneNumber())
            .userAddress(userDTO.getUserAddress())
            .roles(userDTO.getRoles())
            .createdAt(userDTO.getCreatedAt())
            .updatedAt(userDTO.getUpdatedAt())
            .photoId(photoId)
            .intro(userDTO.getIntro())
            .isMarketed(userDTO.getIsMarketed())
            .userStatus(userDTO.getUserStatus())
            .build();
    }
}
