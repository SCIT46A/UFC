package app.scit46.ufc.dto;

import app.scit46.ufc.entity.ProductDeliveryEntity;
import lombok.*;

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
