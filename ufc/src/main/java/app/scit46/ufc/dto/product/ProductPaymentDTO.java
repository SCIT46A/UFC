package app.scit46.ufc.dto.product;

import java.time.LocalDateTime;

import app.scit46.ufc.dto.UserDTO;
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
    private ProductDTO product; // ✅ ProductDTO 포함
    private UserDTO purchasedBy; // ✅ UserDTO 포함
    private Integer price;
    private Byte stock;
    private LocalDateTime purchasedDate;
    private String receiptNumber;
    private String status;
    private Boolean isAdjust;

    public static ProductPaymentDTO toDTO(ProductPaymentEntity entity) {
        return ProductPaymentDTO.builder()
                .payId(entity.getPayId())
                .product(entity.getProduct() != null ? ProductDTO.toDTO(entity.getProduct()) : null) // ✅ ProductDTO 변환
                .purchasedBy(entity.getPurchasedBy() != null ? UserDTO.toDTO(entity.getPurchasedBy()) : null) // ✅ UserDTO 변환
                .price(entity.getPrice())
                .stock(entity.getStock())
                .purchasedDate(entity.getPurchasedDate())
                .receiptNumber(entity.getReceiptNumber())
                .status(entity.getStatus())
                .isAdjust(entity.getIsAdjust())
                .build();
    }
}
