package app.scit46.ufc.dto.custom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
public class CreateCampaignDTO {
    private List<String> tagList;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime sendDate;
    private String description;
    private List<Map<String, Number>> fundingItems;
    private List<Map<String, ?>> rewardList;
    private String imageUrl;
    private Long imageId;
}
