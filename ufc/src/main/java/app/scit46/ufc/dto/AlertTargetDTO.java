package app.scit46.ufc.dto;

import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.entity.AlertTargetEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AlertTargetDTO {
    private Long alertTargetId;
    private AlertDTO alert; // ✅ AlertDTO 포함
    private UserAlertDTO userAlert; // ✅ UserAlertDTO 포함
    private CampaignDTO targetCampaign; // ✅ CampaignDTO 포함
    private ProductDTO targetProduct; // ✅ ProductDTO 포함
    private BadgeDTO targetBadge; // ✅ BadgeDTO 포함
    private NoticeDTO targetNotice; // ✅ NoticeDTO 포함

    public static AlertTargetDTO toDTO(AlertTargetEntity entity) {
        return AlertTargetDTO.builder()
                .alertTargetId(entity.getAlertTargetId())
                .alert(entity.getAlert() != null ? AlertDTO.toDTO(entity.getAlert()) : null) // ✅ AlertDTO 변환
                .userAlert(entity.getUserAlert() != null ? UserAlertDTO.toDTO(entity.getUserAlert()) : null) // ✅ UserAlertDTO 변환
                .targetCampaign(entity.getTargetCampaign() != null ? CampaignDTO.toDTO(entity.getTargetCampaign()) : null) // ✅ CampaignDTO 변환
                .targetProduct(entity.getTargetProduct() != null ? ProductDTO.toDTO(entity.getTargetProduct()) : null) // ✅ ProductDTO 변환
                .targetBadge(entity.getTargetBadge() != null ? BadgeDTO.toDTO(entity.getTargetBadge()) : null) // ✅ BadgeDTO 변환
                .targetNotice(entity.getTargetNotice() != null ? NoticeDTO.toDTO(entity.getTargetNotice()) : null) // ✅ NoticeDTO 변환
                .build();
    }
}