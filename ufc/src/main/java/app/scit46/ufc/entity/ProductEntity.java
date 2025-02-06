package app.scit46.ufc.entity;

import app.scit46.ufc.dto.ProductDTO;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private CreatorEntity createdBy;

    // OneToMany: ProductPayments.product 참조
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductPaymentEntity> productPayments;

    // OneToMany: ProductDeliveries.product 참조
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductDeliveryEntity> productDeliveries;

    public static ProductEntity toEntity(ProductDTO dto, ItemEntity item, CreatorEntity createdBy) {
        return ProductEntity.builder()
                .productId(dto.getProductId())
                .item(item)
                .stockQuantity(dto.getStockQuantity())
                .createdBy(createdBy)
                .build();
    }
}
