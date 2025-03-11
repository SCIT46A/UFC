package app.scit46.ufc.entity.product;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.ItemEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @Lob
    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;

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
                .price(dto.getPrice())
                .status(dto.getStatus())
                .content(dto.getContent())
                .stockQuantity(dto.getStockQuantity())
                .createdBy(CreatorEntity.toEntity(dto.getCreatedBy()))
                .build();
    }
}