package app.scit46.ufc.dto;

import app.scit46.ufc.entity.LikeEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LikeDTO {
    private Long likeId;
    private Long userId;
    private Long creatorId;
    private Long campaignId;
    private Long productId;

    public static LikeDTO toDTO(LikeEntity entity) {
        return LikeDTO.builder()
                .likeId(entity.getLikeId())
                .userId(entity.getUser() != null ? entity.getUser().getUserId() : null)
                .creatorId(entity.getCreator() != null ? entity.getCreator().getCreatorId() : null)
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getCampaignId() : null)
                .productId(entity.getProduct() != null ? entity.getProduct().getProductId() : null)
                .build();
    }
}
