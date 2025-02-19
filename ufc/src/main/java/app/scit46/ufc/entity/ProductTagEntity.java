package app.scit46.ufc.entity;

import app.scit46.ufc.dto.ProductTagDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "ProductTags")
public class ProductTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_tag_id")
    private Long pTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private TagEntity tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    public static ProductTagEntity toEntity(ProductTagDTO dto, TagEntity tag, ProductEntity product) {
        return ProductTagEntity.builder()
                .pTagId(dto.getPTagId())
                .tag(tag)
                .product(product)
                .build();
    }
}
