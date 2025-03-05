package app.scit46.ufc.dto.custom;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CampaignWithGoalsDTO {

    private Long campaignId;
    private String type;          // "campaign"
    private String imageId;
    private String sellerName;
    private String campaignTitle;
    private String campaignDescription;
    private Integer campaignDonors;  // 캠페인 전체 후원자 수

    // 해당 캠페인에 속한 목표 목록
    private List<GoalInfo> goals;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class GoalInfo {
        private Long goalId;
        private String goalTitle;
        private Integer requiredQuantity;
        private Integer donatedQuantity;
        private Double donationPercentage;
        private Integer totalDonors;
    }
}
