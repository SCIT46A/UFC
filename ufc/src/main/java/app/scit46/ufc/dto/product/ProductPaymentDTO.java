package app.scit46.ufc.dto.product;

import java.time.LocalDateTime;

import app.scit46.ufc.entity.product.ProductPaymentEntity;
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
public class ProductPaymentDTO {
    private Long payId;
    private Long productId;
    private Long purchasedById;
    private Integer price;
    private Byte stock;
    private LocalDateTime purchasedDate;
    private String receiptNumber;
    private String status;
    private Boolean isAdjust;

    public static ProductPaymentDTO toDTO(ProductPaymentEntity entity) {
        return ProductPaymentDTO.builder()
                .payId(entity.getPayId())
                .productId(entity.getProduct() != null ? entity.getProduct().getProductId() : null)
                .purchasedById(entity.getPurchasedBy() != null ? entity.getPurchasedBy().getUserId() : null)
                .price(entity.getPrice())
                .stock(entity.getStock())
                .purchasedDate(entity.getPurchasedDate())
                .receiptNumber(entity.getReceiptNumber())
                .status(entity.getStatus())
                .isAdjust(entity.getIsAdjust())
                .build();
    }
}
