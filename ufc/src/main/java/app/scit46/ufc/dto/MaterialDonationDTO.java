package app.scit46.ufc.dto;

import app.scit46.ufc.dto.campaign.CampaignDTO;
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
    private CampaignDTO campaign; // ✅ CampaignDTO 포함
    private UserDTO user; // ✅ UserDTO 포함
    private MaterialDTO material; // ✅ MaterialDTO 포함
    private Integer quantity;
    private String status;
    private LocalDateTime donatedDate;
    private String invoice;

    public String getCourierName() {
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
                .build();
    }
}