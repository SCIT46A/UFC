package app.scit46.ufc.dto;

import app.scit46.ufc.entity.RewardDeliveryEntity;
import lombok.*;
import java.time.LocalDateTime;

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
    private Long campaignId;
    private Long donationId;

    public static RewardDeliveryDTO toDTO(RewardDeliveryEntity entity) {
        return RewardDeliveryDTO.builder()
                .rDeliveryId(entity.getRDeliveryId())
                .invoice(entity.getInvoice())
                .status(entity.getStatus())
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getCampaignId() : null)
                .donationId(entity.getDonation() != null ? entity.getDonation().getDonationId() : null)
                .build();
    }
}
