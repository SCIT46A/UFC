package app.scit46.ufc.dto.product;

import app.scit46.ufc.entity.product.ProductTagEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductTagDTO {
    private Long pTagId;
    private Long productId;
    private Integer tagId;

    public static ProductTagDTO toDTO(ProductTagEntity entity) {
        return ProductTagDTO.builder()
                .pTagId(entity.getPTagId())
                .productId(entity.getProduct() != null ? entity.getProduct().getProductId() : null)
                .tagId(entity.getTag() != null ? entity.getTag().getTagId() : null)
                .build();
    }
}
