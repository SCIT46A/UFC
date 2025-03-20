package app.scit46.ufc.dto.custom;

import java.beans.ConstructorProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IntroPageCampaignDTO {

    private Long campaignId;
    private String type;
    private String imageId;
    private String sellerName;
    private String campaignTitle;
    private String campaignDescription;

    private Long goalId;
    private String goalTitle;
    private Integer requiredQuantity;

    private Integer donatedQuantity;
    private Double donationPercentage;
    private Integer totalDonors;

    // 캠페인 전체 후원자 수 (고유)
    private Integer campaignDonors;

    @ConstructorProperties({
            "campaignId", "type", "imageId", "sellerName", "campaignTitle", "campaignDescription",
            "goalId", "goalTitle", "requiredQuantity",
            "donatedQuantity", "donationPercentage", "totalDonors", "campaignDonors"
    })
    public IntroPageCampaignDTO(
            Number campaignId,
            String type,
            String imageId,
            String sellerName,
            String campaignTitle,
            String campaignDescription,
            Number goalId,
            String goalTitle,
            Number requiredQuantity,
            Number donatedQuantity,
            Number donationPercentage,
            Number totalDonors,
            Number campaignDonors
    ) {
        this.campaignId = (campaignId == null) ? null : campaignId.longValue();
        this.type = type;
        this.imageId = imageId;
        this.sellerName = sellerName;
        this.campaignTitle = campaignTitle;
        this.campaignDescription = campaignDescription;
        this.goalId = (goalId == null) ? null : goalId.longValue();
        this.goalTitle = goalTitle;
        this.requiredQuantity = (requiredQuantity == null) ? null : requiredQuantity.intValue();
        this.donatedQuantity = (donatedQuantity == null) ? null : donatedQuantity.intValue();
        this.donationPercentage = (donationPercentage == null) ? null : donationPercentage.doubleValue();
        this.totalDonors = (totalDonors == null) ? null : totalDonors.intValue();
        this.campaignDonors = (campaignDonors == null) ? null : campaignDonors.intValue();
    }
}
