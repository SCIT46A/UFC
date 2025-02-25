package app.scit46.ufc.entity.product;

import app.scit46.ufc.dto.product.ProductItemDTO;
import app.scit46.ufc.entity.ItemEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "ProductItems")
public class ProductItemEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private int price;

    public static ProductItemEntity toEntity(ProductItemDTO dto) {
        return ProductItemEntity.builder()
                .id(dto.getId())
                .item(ItemEntity.toEntity(dto.getItem()))
                .product(ProductEntity.toEntity(dto.getProduct()))
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .build();
    }
}
