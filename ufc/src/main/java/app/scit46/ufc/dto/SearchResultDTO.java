package app.scit46.ufc.dto;

import java.beans.ConstructorProperties;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor  // JPA 등에서 필요
@ToString
public class SearchResultDTO {
    // 원본 id (캠페인 또는 상품의 id)
    private Long originalId;
    // 타입: "campaign" 또는 "product"
    private String type;
    // 이미지 id (PublicPhotoEntity의 photo_id)
    private String imageId;
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
    // 좋아요 수
    private Integer likes;
    // 태그 목록
    private List<String> tags;
    // ✅ 사용자가 좋아요를 눌렀는지 여부 추가
    private Boolean isLiked;

    /**
     * Native Query 결과 매핑용 단일 생성자
     * 숫자형 컬럼은 Number 타입으로 받고 내부에서 원하는 타입으로 변환합니다.
     * createdDate는 MySQL DATETIME이 Timestamp로 반환되므로 Timestamp로 받고 LocalDateTime으로 변환합니다.
     * tags는 GROUP_CONCAT 결과(문자열)를 받아서 콤마 기준으로 분리합니다.
     */
    @ConstructorProperties({
            "originalId", "type", "imageId", "sellerName", "title", "description",
            "price", "remainingDays", "donatedQuantity", "donationPercentage",
            "createdDate", "likes", "tags", "isLiked"
    })
    public SearchResultDTO(Number originalId, String type, String imageId, String sellerName, String title,
                           String description, Number price, Number remainingDays, Number donatedQuantity,
                           Number donationPercentage, Timestamp createdDate, Number likes, String tags,
                           Number isLiked) {
        this.originalId = (originalId == null) ? null : originalId.longValue();
        this.type = type;
        this.imageId = imageId; // 수정됨: imageId를 String 그대로 받음
        this.sellerName = sellerName;
        this.title = title;
        this.description = description;
        this.price = (price == null) ? null : price.intValue();
        this.remainingDays = (remainingDays == null) ? null : remainingDays.longValue();
        this.donatedQuantity = (donatedQuantity == null) ? null : donatedQuantity.intValue();
        this.donationPercentage = (donationPercentage == null) ? null : donationPercentage.doubleValue();
        this.createdDate = (createdDate == null) ? null : createdDate.toLocalDateTime();
        this.likes = (likes == null) ? null : likes.intValue();
        this.tags = (tags != null && !tags.trim().isEmpty())
                ? Arrays.asList(tags.split(","))
                : Collections.emptyList();
        this.isLiked = (isLiked != null && isLiked.intValue() == 1);
    }

}
