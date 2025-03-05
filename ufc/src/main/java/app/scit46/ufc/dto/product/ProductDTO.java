package app.scit46.ufc.dto.product;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.dto.ItemDTO;
import app.scit46.ufc.entity.product.ProductEntity;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductDTO {
    private Long productId;
    private ItemDTO item; // ✅ ItemDTO 포함
    private Integer stockQuantity;
    private CreatorDTO createdBy; // ✅ CreatorDTO 포함

    private Integer price;
    private int status;
    private LocalDateTime createTime;

    public static ProductDTO toDTO(ProductEntity entity) {
        return ProductDTO.builder()
                .productId(entity.getProductId())
                .item(entity.getItem() != null ? ItemDTO.toDTO(entity.getItem()) : null) // ✅ ItemDTO 변환
                .stockQuantity(entity.getStockQuantity())
                .createdBy(entity.getCreatedBy() != null ? CreatorDTO.toDTO(entity.getCreatedBy()) : null) // ✅ CreatorDTO 변환
                .build();
    }
}