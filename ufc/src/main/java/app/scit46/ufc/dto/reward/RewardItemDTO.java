package app.scit46.ufc.dto.reward;

import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.ItemDTO;
import app.scit46.ufc.entity.reward.RewardItemEntity;
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
public class RewardItemDTO {
    private Long id;
    private RewardDTO reward;
    private ItemDTO item;
    private int quantity;

    public static RewardItemDTO toDTO(RewardItemEntity entity) {
        if (entity == null) return null;
        return RewardItemDTO.builder()
                .id(entity.getId())
                .item(entity.getItem() != null ?
                        ItemDTO.builder()
                                .itemId(entity.getItem().getItemId())
                                .name(entity.getItem().getName())
                                .description(entity.getItem().getDescription())
                                .photo(ImageUrlDTO.toDTO(entity.getItem().getPhoto()))
                                .build() : null)
                .reward(entity.getReward() != null ?
                        RewardDTO.builder()
                                .rewardId(entity.getReward().getRewardId())
                                .rewardName(entity.getReward().getRewardName())
                                .build() : null)
                .quantity(entity.getQuantity())
                .build();
    }
}