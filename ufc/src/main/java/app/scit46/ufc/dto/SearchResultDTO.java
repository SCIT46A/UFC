package app.scit46.ufc.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SearchResultDTO {
    // 원본 id (캠페인 또는 상품의 id)
    private Long originalId;

    // 타입: "campaign" 또는 "product"
    private String type;

    // 이미지 id (PublicPhotoEntity의 photo_id)
    private Long imageId;

    // 판매자 이름
    private String sellerName;

    // 제목
    private String title;

    // 설명
    private String description;

    // 상품인 경우 가격, 캠페인인 경우 null 처리
    private Integer price;

    // 캠페인인 경우 남은 기간(일수), 상품인 경우 null 처리
    private Long remainingDays;

    // 캠페인용 추가 필드: 기부받은 총 수량
    private Integer donatedQuantity;

    // 캠페인용 추가 필드: 기부 퍼센티지 (0~100)
    private Double donationPercentage;

    // 생성일 (정렬 기준 등으로 사용)
    private LocalDateTime createdDate;
    private Integer likes;           // 좋아요 수
    private List<String> tags;       // 태그 목록
}
