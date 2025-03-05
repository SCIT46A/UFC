package app.scit46.ufc.entity.product;

import app.scit46.ufc.dto.product.ProductDeliveryDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "ProductDeliveries")
public class ProductDeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_delivery_id")
    private Long pDeliveryId;

    @Column(name = "invoice", nullable = false, length = 40)
    private String invoice;

    @Column(name = "status", length = 100)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_id")
    private ProductPaymentEntity pay;

    public static ProductDeliveryEntity toEntity(ProductDeliveryDTO dto) {
        return ProductDeliveryEntity.builder()
                .pDeliveryId(dto.getPDeliveryId())
                .invoice(dto.getInvoice())
                .status(dto.getStatus())
                .product(ProductEntity.builder().productId(dto.getProduct().getProductId()).build())
                .pay(ProductPaymentEntity.builder().payId(dto.getPay().getPayId()).build())
                .build();
    }
}
