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
    private RewardEntity reward;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity item;
    
    @Column(name = "quantity")
    private int quantity;

    public static RewardItemEntity toEntity(RewardItemDTO dto) {
        return RewardItemEntity.builder()
                .reward(RewardEntity.toEntity(dto.getReward()))
                .item(ItemEntity.toEntity(dto.getItem()))
                .quantity(dto.getQuantity())
                .build();
    }
}
