package app.scit46.ufc.entity;

import java.util.List;
import java.util.stream.Collectors;

import app.scit46.ufc.dto.ItemDTO;
import app.scit46.ufc.entity.product.ProductEntity;
import app.scit46.ufc.entity.product.ProductItemEntity;
import app.scit46.ufc.entity.reward.RewardEntity;
import app.scit46.ufc.entity.reward.RewardItemEntity;
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

// 리워드, 판매 상품에 대한 기본 정보

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "photo_id")
    private ImageUrlEntity photo;

    // OneToMany: Products.item 참조
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<ProductItemEntity> productItems;

    // OneToMany: Reward.items 참조
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<RewardItemEntity> rewardItems;

    public static ItemEntity toEntity(ItemDTO dto) {
        if (dto == null) return null;

        return ItemEntity.builder()
                .itemId(dto.getItemId())
                .name(dto.getName() != null ? dto.getName() : "")
                .description(dto.getDescription() != null ? dto.getDescription() : "")
                .photo(dto.getPhoto() != null ? ImageUrlEntity.toEntity(dto.getPhoto()) : null)
                .build();
    }
}
