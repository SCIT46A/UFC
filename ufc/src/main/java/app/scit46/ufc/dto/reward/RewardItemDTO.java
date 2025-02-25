package app.scit46.ufc.dto.reward;

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
        return RewardItemDTO.builder()
                .id(entity.getId())
                .reward(RewardDTO.toDTO(entity.getReward()))
                .item(ItemDTO.toDTO(entity.getItem()))
                .quantity(entity.getQuantity())
                .build();   
    }
}