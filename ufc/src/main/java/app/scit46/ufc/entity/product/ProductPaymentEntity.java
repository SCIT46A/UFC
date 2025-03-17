package app.scit46.ufc.entity.product;

import java.time.LocalDateTime;
import java.util.List;

import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.dto.product.ProductPaymentDTO;
import app.scit46.ufc.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

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
    @CreationTimestamp
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

    public static ProductPaymentEntity toEntity(ProductPaymentDTO dto) {
        return ProductPaymentEntity.builder()
                .payId(dto.getPayId())
                .product(ProductEntity.builder().productId(dto.getProduct().getProductId()).build())
                .purchasedBy(UserEntity.builder().userId(dto.getPurchasedBy().getUserId()).build())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .purchasedDate(dto.getPurchasedDate())
                .receiptNumber(dto.getReceiptNumber())
                .status(dto.getStatus())
                .isAdjust(dto.getIsAdjust())
                .build();
    }
}
