package app.scit46.ufc.entity;

import java.util.List;

import app.scit46.ufc.dto.ItemDTO;
import app.scit46.ufc.entity.product.ProductEntity;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Items")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private ImageUrlEntity photo;

    // OneToMany: Products.item 참조
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<ProductEntity> products;

    public static ItemEntity toEntity(ItemDTO dto) {
        return ItemEntity.builder()
                //.itemId(dto.getItemId())  // 기본값 자동 생성이므로 주석처리
                .name(dto.getName() != null ? dto.getName() : "")
                .description(dto.getDescription() != null ? dto.getDescription() : "")
                .price(dto.getPrice() != null ? dto.getPrice() : 0)
                .photo(dto.getPhoto() != null ? ImageUrlEntity.toEntity(dto.getPhoto()) : null)
                .build();
    }
}
