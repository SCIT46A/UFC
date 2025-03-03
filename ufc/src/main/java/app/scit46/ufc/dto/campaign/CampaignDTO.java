package app.scit46.ufc.dto.campaign;

import java.time.LocalDateTime;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.cloudflare.Image;
import app.scit46.ufc.entity.campaign.CampaignEntity;
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
public class CampaignDTO {
    private Long campaignId;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime sendDate;
    private LocalDateTime createdDate;
    private CreatorDTO createdBy; // ✅ CreatorDTO 포함
    private Boolean isSuccess;
    private ImageUrlDTO photo; // ✅ PhotoDTO 포함
    private Integer campaignStatus;

    public static CampaignDTO toDTO(CampaignEntity entity) {
        return CampaignDTO.builder()
                .campaignId(entity.getCampaignId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .sendDate(entity.getSendDate())
                .createdDate(entity.getCreatedDate())
                .isSuccess(entity.getIsSuccess())
                .createdBy(entity.getCreatedBy() != null ? CreatorDTO.toDTO(entity.getCreatedBy()) : null) // ✅
                                                                                                           // CreatorDTO
                                                                                                           // 변환
                .photo(entity.getPhoto() != null ? ImageUrlDTO.toDTO(entity.getPhoto()) : null) // ✅ PhotoDTO 변환
                .campaignStatus(entity.getCampaignStatus())
                .build();
    }
}
