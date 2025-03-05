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
public class GenerateCampaignDTO {
    // Tag
    private String userName; // 헤더에 존재하는 사용자 이름(검토 필요)
    private List<String> tagList; // 태그 리스트 -> 캠페인과 연결된 CampaignTagDTO/Entity와 연계 필요
    // Campaign
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime sendDate;
    // Reward / Material
    private List<RewardFundingDTO> fundingItems;    // 캠페인에 필요한 총 재료 리스트
    private List<RewardListDTO> rewardList;         // 캠페인에서 제공할 리워드와 각각의 필요한 재료 리스트
    // Image 선행 로직으로 이미지가 먼저 업로드 된 후 반환된 이미지 ID를 받아옴
    private String imageId;
    //private MultipartFile image;

    //private String imageUrl; // 이미지 자체를 받아오는 것으로 변경
    //private Long imageId; // 이미지 자체를 받아오는 것으로 변경
}
