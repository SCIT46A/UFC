package app.scit46.ufc.dto;

import app.scit46.ufc.entity.MaterialDonationEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MaterialDonationDTO {
    private Long donationId;
    private Long campaignId;
    private Long userId;
    private Long materialId;
    private Integer quantity;
    private String status;
    private LocalDateTime donatedDate;

    public static MaterialDonationDTO toDTO(MaterialDonationEntity entity) {
        return MaterialDonationDTO.builder()
                .donationId(entity.getDonationId())
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getCampaignId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getUserId() : null)
                .materialId(entity.getMaterial() != null ? entity.getMaterial().getMaterialId() : null)
                .quantity(entity.getQuantity())
                .status(entity.getStatus())
                .donatedDate(entity.getDonatedDate())
                .build();
    }
}
