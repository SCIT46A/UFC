package app.scit46.ufc.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.entity.product.ProductPaymentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    private ImageUrlEntity photoId;

    @Column(name = "intro")
    private String intro;

    @Column(name = "is_marketed")
    private int isMarketed;

    @Column(name = "user_status")
    private int userStatus;

    @Column(name = "status_reason")
    private String statusReason;

    // OneToMany: Creators.own_user 참조
    @OneToMany(mappedBy = "ownUser", fetch = FetchType.LAZY)
    private List<CreatorEntity> creators;

    // OneToMany: MaterialsDonations.user 참조
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<MaterialDonationEntity> materialDonations;

    // OneToMany: ProductPayments.purchased_by 참조
    @OneToMany(mappedBy = "purchasedBy", fetch = FetchType.LAZY)
    private List<ProductPaymentEntity> productPayments;

    // OneToMany: Reports.reported_by 참조
    @OneToMany(mappedBy = "reportedBy", fetch = FetchType.LAZY)
    private List<ReportEntity> reportsMade;

    // OneToMany: Reports.user 참조 (신고당한 사용자)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ReportEntity> reportsReceived;

    // OneToMany: Likes.user 참조
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<LikeEntity> likes;

    // OneToMany: UserAlert.user 참조
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserAlertEntity> userAlerts;

    public static UserEntity toEntity(UserDTO userDTO) {
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
                .photoId(ImageUrlEntity.toEntity(userDTO.getPhoto()))
                .intro(userDTO.getIntro())
                .isMarketed(userDTO.getIsMarketed())
                .userStatus(userDTO.getUserStatus())
                .statusReason(userDTO.getStatusReason())
                .build();
    }
}