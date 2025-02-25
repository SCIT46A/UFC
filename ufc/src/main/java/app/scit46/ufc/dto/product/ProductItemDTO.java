package app.scit46.ufc.dto.product;

import app.scit46.ufc.dto.ItemDTO;
import app.scit46.ufc.entity.product.ProductItemEntity;
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
@Builder
@ToString
public class ProductItemDTO {
 
    private Long id;
    private ItemDTO item;
    private ProductDTO product;
    private int quantity;
    private int price;

    public static ProductItemDTO toDTO(ProductItemEntity entity) {
        return ProductItemDTO.builder()
                .id(entity.getId())
                .item(ItemDTO.toDTO(entity.getItem()))
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .product(ProductDTO.toDTO(entity.getProduct()))
                .build();
    }
}
