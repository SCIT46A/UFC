package app.scit46.ufc.dto.product;

import app.scit46.ufc.entity.product.ProductEntity;
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
public class ProductDTO {
    private Long productId;
    private Long itemId;
    private Integer stockQuantity;
    private Long createdById;

    public static ProductDTO toDTO(ProductEntity entity) {
        return ProductDTO.builder()
                .productId(entity.getProductId())
                .itemId(entity.getItem() != null ? entity.getItem().getItemId() : null)
                .stockQuantity(entity.getStockQuantity())
                .createdById(entity.getCreatedBy() != null ? entity.getCreatedBy().getCreatorId() : null)
                .build();
    }
}
