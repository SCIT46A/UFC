package app.scit46.ufc.entity;

import app.scit46.ufc.dto.ProductPaymentDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "ProductPayments")
public class ProductPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_id")
    private Long payId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchased_by")
    private UserEntity purchasedBy;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "stock", nullable = false)
    private Byte stock;

    @Column(name = "purchased_date", nullable = false)
    private LocalDateTime purchasedDate;

    @Column(name = "receipt_number", nullable = false, length = 100)
    private String receiptNumber;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "is_adjust", nullable = false)
    private Boolean isAdjust;

    // OneToMany: ProductDeliveryEntity.pay 참조
    @OneToMany(mappedBy = "pay", fetch = FetchType.LAZY)
    private List<ProductDeliveryEntity> productDeliveries;

    public static ProductPaymentEntity toEntity(ProductPaymentDTO dto, ProductEntity product, UserEntity purchasedBy) {
        return ProductPaymentEntity.builder()
                .payId(dto.getPayId())
                .product(product)
                .purchasedBy(purchasedBy)
                .price(dto.getPrice())
                .stock(dto.getStock())
                .purchasedDate(dto.getPurchasedDate())
                .receiptNumber(dto.getReceiptNumber())
                .status(dto.getStatus())
                .isAdjust(dto.getIsAdjust())
                .build();
    }
}
