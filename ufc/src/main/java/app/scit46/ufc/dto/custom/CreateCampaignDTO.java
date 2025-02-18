package app.scit46.ufc.dto.custom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

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
    // Tag
    private List<String> tagList;
    // Campaign
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime sendDate;
    // Reward / Material
    private List<RewardListDTO> fundingItems;
    private List<RewardListDTO> rewardList;
    // Image
    private MultipartFile image;

    @Getter
    @Setter
    public class RewardListDTO {
        private String name; // -> ItemDTO
        private String amount; // -> Reward
    }

    //private String imageUrl; // 이미지 자체를 받아오는 것으로 변경
    //private Long imageId; // 이미지 자체를 받아오는 것으로 변경
}
