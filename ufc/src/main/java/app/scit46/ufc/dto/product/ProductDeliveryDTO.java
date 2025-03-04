package app.scit46.ufc.dto.product;

import app.scit46.ufc.entity.product.ProductDeliveryEntity;
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
public class ProductDeliveryDTO {
    private Long pDeliveryId;
    private String invoice;
    private String status;
    private ProductDTO product; // ✅ ProductDTO 포함
    private ProductPaymentDTO pay; // ✅ ProductPaymentDTO 포함

    public static ProductDeliveryDTO toDTO(ProductDeliveryEntity entity) {
        return ProductDeliveryDTO.builder()
                .pDeliveryId(entity.getPDeliveryId())
                .invoice(entity.getInvoice())
                .status(entity.getStatus())
                .product(entity.getProduct() != null ? ProductDTO.toDTO(entity.getProduct()) : null) // ✅ ProductDTO 변환
                .pay(entity.getPay() != null ? ProductPaymentDTO.toDTO(entity.getPay()) : null) // ✅ ProductPaymentDTO 변환
                .build();
    }
}
