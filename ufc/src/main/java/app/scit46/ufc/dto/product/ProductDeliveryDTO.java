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
    private Long productId;
    private Long payId;

    public static ProductDeliveryDTO toDTO(ProductDeliveryEntity entity) {
        return ProductDeliveryDTO.builder()
                .pDeliveryId(entity.getPDeliveryId())
                .invoice(entity.getInvoice())
                .status(entity.getStatus())
                .productId(entity.getProduct() != null ? entity.getProduct().getProductId() : null)
                .payId(entity.getPay() != null ? entity.getPay().getPayId() : null)
                .build();
    }
}
