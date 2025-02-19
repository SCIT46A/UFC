package app.scit46.ufc.entity;

import app.scit46.ufc.dto.ItemDTO;
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

    public static ItemEntity toEntity(ItemDTO dto, ImageUrlEntity photo) {
        return ItemEntity.builder()
                .itemId(dto.getItemId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .photo(photo)
                .build();
    }
}
