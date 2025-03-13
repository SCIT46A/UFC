package app.scit46.ufc.entity;

import app.scit46.ufc.dto.BadgeDTO;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Badges")
public class BadgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private Long badgeId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private ImageUrlEntity photo;

    // OneToMany: UserBadges.badge 참조
    @OneToMany(mappedBy = "badge", fetch = FetchType.LAZY)
    private List<UserBadgeEntity> userBadges;

    // OneToMany: AlertTarget.target_badge 참조
    @OneToMany(mappedBy = "targetBadge", fetch = FetchType.LAZY)
    private List<AlertTargetEntity> alertTargets;

    public static BadgeEntity toEntity(BadgeDTO dto) {
        return BadgeEntity.builder()
                .badgeId(dto.getBadgeId())
                .name(dto.getName())
                .description(dto.getDescription())
                .photo(ImageUrlEntity.builder().imageId(dto.getPhoto().getImageId()).build())
                .build();
    }
}
