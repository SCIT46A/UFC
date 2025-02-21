package app.scit46.ufc.dto.campaign;

import java.time.LocalDateTime;

import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.entity.campaign.CampaignReviewEntity;
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
public class CampaignReviewDTO {
    private Long cReviewId;
    private String content;
    private LocalDateTime createdDate;
    private Double rated;
    private UserDTO reviewedBy; // ✅ UserDTO 포함
    private CampaignDTO campaignedBy; // ✅ CampaignDTO 포함

    public static CampaignReviewDTO toDTO(CampaignReviewEntity entity) {
        return CampaignReviewDTO.builder()
                .cReviewId(entity.getCReviewId())
                .content(entity.getContent())
                .createdDate(entity.getCreatedDate())
                .rated(entity.getRated())
                .reviewedBy(entity.getReviewedBy() != null ? UserDTO.toDTO(entity.getReviewedBy()) : null) // ✅ UserDTO 변환
                .campaignedBy(entity.getCampaignedBy() != null ? CampaignDTO.toDTO(entity.getCampaignedBy()) : null) // ✅ CampaignDTO 변환
                .build();
    }
}