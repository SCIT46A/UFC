package app.scit46.ufc.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.reward.RewardDeliveryDTO;
import app.scit46.ufc.entity.MaterialDonationEntity;
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
public class MaterialDonationDTO {
    private Long donationId;
    private CampaignDTO campaign; // ✅ CampaignDTO 포함
    private UserDTO user; // ✅ UserDTO 포함
    private MaterialDTO material; // ✅ MaterialDTO 포함
    private Integer quantity;
    private String status;
    private LocalDateTime donatedDate;
    private String invoice;
    private List<RewardDeliveryDTO> rewardDeliveries;

    public String getCourierId() {
        return invoice != null ? invoice.split("#")[0] : "";
    }

    public String getTrackingNumber() {
        return invoice != null ? invoice.split("#")[1] : "";
    }

    public static MaterialDonationDTO toDTO(MaterialDonationEntity entity) {
        return MaterialDonationDTO.builder()
                .donationId(entity.getDonationId())
                .campaign(entity.getCampaign() != null ? CampaignDTO.toDTO(entity.getCampaign()) : null) // ✅
                                                                                                         // CampaignDTO
                                                                                                         // 변환
                .user(entity.getUser() != null ? UserDTO.toDTO(entity.getUser()) : null) // ✅ UserDTO 변환
                .material(entity.getMaterial() != null ? MaterialDTO.toDTO(entity.getMaterial()) : null) // ✅
                                                                                                         // MaterialDTO
                                                                                                         // 변환
                .quantity(entity.getQuantity())
                .status(entity.getStatus())
                .donatedDate(entity.getDonatedDate())
                .invoice(entity.getInvoice())
                .rewardDeliveries(entity.getRewardDeliveries() != null ? entity.getRewardDeliveries().stream()
                        .map(RewardDeliveryDTO::toDTO)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public static MaterialDonationDTO toDTOMinimal(MaterialDonationEntity entity) {
        if (entity == null)
            return null;
        return MaterialDonationDTO.builder()
                .donationId(entity.getDonationId())
                .invoice(entity.getInvoice())
                .build();
    }

}