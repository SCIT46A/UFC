package app.scit46.ufc.dto;

import java.util.List;
import java.util.stream.Collectors;

import app.scit46.ufc.dto.product.ProductItemDTO;
import app.scit46.ufc.dto.reward.RewardItemDTO;
import app.scit46.ufc.entity.ItemEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ItemDTO {
    private Long itemId;
    private String name;
    private String description;
    private ImageUrlDTO photo; // ✅ PhotoDTO 포함
    private List<RewardItemDTO> rewardItems; // ✅ RewardItemDTO 포함
    private List<ProductItemDTO> productItems; // ✅ ProductItemDTO 포함

    public static ItemDTO toDTO(ItemEntity entity) {
        return ItemDTO.builder()
                .itemId(entity.getItemId())
                .name(entity.getName())
                .description(entity.getDescription())
                .photo(entity.getPhoto() != null ? ImageUrlDTO.toDTO(entity.getPhoto()) : null)
                .rewardItems(entity.getRewardItems().stream()
                        .map(RewardItemDTO::toDTO)
                        .collect(Collectors.toList())) // ✅ RewardItemDTO 변환
                .productItems(entity.getProductItems().stream()
                        .map(ProductItemDTO::toDTO)
                        .collect(Collectors.toList())) // ✅ ProductItemDTO 변환
                .build();
    }
}