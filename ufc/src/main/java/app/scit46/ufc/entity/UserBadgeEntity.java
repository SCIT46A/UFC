package app.scit46.ufc.entity;

import app.scit46.ufc.dto.UserBadgeDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "UserBadges")
public class UserBadgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_badge_id")
    private Long userBadgeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id")
    private BadgeEntity badge;

    public static UserBadgeEntity toEntity(UserBadgeDTO dto) {
        return UserBadgeEntity.builder()
                .userBadgeId(dto.getUserBadgeId())
                .user(UserEntity.builder().userId(dto.getUser().getUserId()).build())
                .badge(BadgeEntity.builder().badgeId(dto.getBadge().getBadgeId()).build())
                .build();
    }
}
