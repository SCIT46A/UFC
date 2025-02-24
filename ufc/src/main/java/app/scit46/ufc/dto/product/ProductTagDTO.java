package app.scit46.ufc.dto.product;

import app.scit46.ufc.dto.TagDTO;
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
    private ProductDTO product; // ✅ ProductDTO 포함
    private TagDTO tag; // ✅ TagDTO 포함

    public static ProductTagDTO toDTO(ProductTagEntity entity) {
        return ProductTagDTO.builder()
                .pTagId(entity.getPTagId())
                .product(entity.getProduct() != null ? ProductDTO.toDTO(entity.getProduct()) : null) // ✅ ProductDTO 변환
                .tag(entity.getTag() != null ? TagDTO.toDTO(entity.getTag()) : null) // ✅ TagDTO 변환
                .build();
    }
}