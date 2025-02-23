package app.scit46.ufc.entity.product;

import app.scit46.ufc.dto.TagDTO;
import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.dto.product.ProductTagDTO;
import app.scit46.ufc.entity.TagEntity;
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

    public static ProductTagEntity toEntity(ProductTagDTO dto, TagDTO tag, ProductDTO product) {
        return ProductTagEntity.builder()
                .pTagId(dto.getPTagId())
                .tag(TagEntity.builder().tagId(tag.getTagId()).build())
                .product(ProductEntity.builder().productId(product.getProductId()).build())
                .build();
    }
}
