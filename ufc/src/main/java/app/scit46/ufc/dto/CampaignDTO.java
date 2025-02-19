package app.scit46.ufc.dto;

import java.time.LocalDateTime;

import app.scit46.ufc.entity.CampaignEntity;
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
    // createdBy와 photo는 필요에 따라 DTO 또는 ID(Long)로 처리 가능
    private Long createdById;
    private Boolean isSuccess;
    private Long photoId;
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
                .createdById(entity.getCreatedBy().getCreatorId())
                //.createdById(entity.getCreatedBy() != null ? entity.getCreatedBy().getCreatorId() : null)
                .photoId(entity.getPhoto().getId())
                .campaignStatus(entity.getCampaignStatus())
                .build();
    }
}
