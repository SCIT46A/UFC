package app.scit46.ufc.entity.product;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.ItemEntity;
import org.hibernate.annotations.CreationTimestamp;

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

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private int status;

    @Column(name = "create_time")
    @CreationTimestamp
    private LocalDateTime createTime;

    // OneToMany: ProductPayments.product 참조
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductPaymentEntity> productPayments;

    // OneToMany: ProductDeliveries.product 참조
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductDeliveryEntity> productDeliveries;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductTagEntity> productTags;


    public static ProductEntity toEntity(ProductDTO dto) {
        return ProductEntity.builder()
                .productId(dto.getProductId())
                .item(ItemEntity.toEntity(dto.getItem()))
                .stockQuantity(dto.getStockQuantity())
                .createdBy(CreatorEntity.toEntity(dto.getCreatedBy()))
                .build();
    }
}