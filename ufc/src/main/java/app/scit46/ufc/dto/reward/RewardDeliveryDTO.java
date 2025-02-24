package app.scit46.ufc.dto.reward;

import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
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
public class RewardDeliveryDTO {
    private Long rDeliveryId;
    private String invoice;
    private String status;
    private CampaignDTO campaign; // ✅ CampaignDTO 포함
    private MaterialDonationDTO donation; // ✅ MaterialDonationDTO 포함

    public static RewardDeliveryDTO toDTO(RewardDeliveryEntity entity) {
        return RewardDeliveryDTO.builder()
                .rDeliveryId(entity.getRDeliveryId())
                .invoice(entity.getInvoice())
                .status(entity.getStatus())
                .campaign(entity.getCampaign() != null ? CampaignDTO.toDTO(entity.getCampaign()) : null) // ✅ CampaignDTO 변환
                .donation(entity.getDonation() != null ? MaterialDonationDTO.toDTO(entity.getDonation()) : null) // ✅ MaterialDonationDTO 변환
                .build();
    }
}