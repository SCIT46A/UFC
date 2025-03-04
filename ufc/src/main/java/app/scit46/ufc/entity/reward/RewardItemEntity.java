package app.scit46.ufc.entity.reward;

import app.scit46.ufc.dto.reward.RewardItemDTO;
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

// 캠페인의 보상으로서 제공되는 리워드에 대한 상품 정보

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "RewardItems")
public class RewardItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reward_id")
    private RewardEntity reward; // 리워드 그룹

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity item; //상품

    @Column(name = "quantity")
    private int quantity;   //재고수량 / 한정수량

    public static RewardItemEntity toEntity(RewardItemDTO dto) {
        if (dto == null) return null;

        return RewardItemEntity.builder()
                .id(dto.getId())
                .reward(dto.getReward() != null ?
                       RewardEntity.builder().rewardId(dto.getReward().getRewardId()).build() : null)
                .item(dto.getItem() != null ?
                     ItemEntity.builder().itemId(dto.getItem().getItemId()).build() : null)
                .quantity(dto.getQuantity())
                .build();
    }
}
