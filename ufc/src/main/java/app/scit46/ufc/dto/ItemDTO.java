package app.scit46.ufc.dto;

import app.scit46.ufc.entity.ItemEntity;
import app.scit46.ufc.entity.PublicPhotoEntity;
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
    private PublicPhotoEntity photo;

    public static ItemDTO toDTO(ItemEntity entity) {
        return ItemDTO.builder()
                .itemId(entity.getItemId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .photo(entity.getPhoto())
                .build();
    }
}
