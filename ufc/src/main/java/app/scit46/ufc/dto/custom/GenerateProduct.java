package app.scit46.ufc.dto.custom;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GenerateProduct {
    private List<String> tagList; // 캠페인 태그 리스트
    private String title; // 상품 제목
    private String description; // 상품 설명
    private int price; // 상품 가격
    private int stock; // 상품 재고 수량
    private String userName; // 사용자 이름
    private String imageId; // 이미지 ID
}
