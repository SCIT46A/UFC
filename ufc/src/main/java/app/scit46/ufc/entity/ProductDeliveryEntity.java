package app.scit46.ufc.entity;

import app.scit46.ufc.dto.ProductDeliveryDTO;
import jakarta.persistence.*;
import lombok.*;

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

    public static ProductDeliveryEntity toEntity(ProductDeliveryDTO dto, ProductEntity product, ProductPaymentEntity pay) {
        return ProductDeliveryEntity.builder()
                .pDeliveryId(dto.getPDeliveryId())
                .invoice(dto.getInvoice())
                .status(dto.getStatus())
                .product(product)
                .pay(pay)
                .build();
    }
}
