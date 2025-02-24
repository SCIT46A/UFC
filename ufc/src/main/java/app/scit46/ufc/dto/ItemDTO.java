package app.scit46.ufc.dto;

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
    private Integer price;
    private ImageUrlDTO photo; // ✅ PhotoDTO 포함

    public static ItemDTO toDTO(ItemEntity entity) {
        return ItemDTO.builder()
                .itemId(entity.getItemId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .photo(entity.getPhoto() != null ? ImageUrlDTO.toDTO(entity.getPhoto()) : null) // ✅ PhotoDTO 변환
                .build();
    }
}