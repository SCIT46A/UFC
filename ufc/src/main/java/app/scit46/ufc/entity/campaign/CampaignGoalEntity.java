package app.scit46.ufc.entity.campaign;

import app.scit46.ufc.dto.MaterialDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.campaign.CampaignGoalDTO;
import app.scit46.ufc.entity.MaterialEntity;
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

    public static CampaignGoalEntity toEntity(CampaignGoalDTO dto) {
        return CampaignGoalEntity.builder()
                .goalId(dto.getGoalId())
                .campaign(CampaignEntity.builder().campaignId(dto.getCampaign().getCampaignId()).build())
                .material(MaterialEntity.builder().materialId(dto.getMaterial().getMaterialId()).build())
                .quantityRequired(dto.getQuantityRequired())
                .build();
    }
}
