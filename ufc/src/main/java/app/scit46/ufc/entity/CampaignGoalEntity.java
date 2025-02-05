package app.scit46.ufc.entity;

import app.scit46.ufc.dto.CampaignGoalDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "CampaignGoals")
public class CampaignGoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignEntity campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private MaterialEntity material;

    @Column(name = "quantity_required", nullable = false)
    private Integer quantityRequired;

    public static CampaignGoalEntity toEntity(CampaignGoalDTO dto, CampaignEntity campaign, MaterialEntity material) {
        return CampaignGoalEntity.builder()
                .goalId(dto.getGoalId())
                .campaign(campaign)
                .material(material)
                .quantityRequired(dto.getQuantityRequired())
                .build();
    }
}
