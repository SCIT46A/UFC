package app.scit46.ufc.entity;

import app.scit46.ufc.dto.LikeDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Likes")
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 선택적
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private CreatorEntity creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private CampaignEntity campaign;

    // 선택적
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    public static LikeEntity toEntity(LikeDTO dto, UserEntity user, CreatorEntity creator, CampaignEntity campaign, ProductEntity product) {
        return LikeEntity.builder()
                .likeId(dto.getLikeId())
                .user(user)
                .creator(creator)
                .campaign(campaign)
                .product(product)
                .build();
    }
}
