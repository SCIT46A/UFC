package app.scit46.ufc.dto.custom;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
// Jackson 직렬화 문제로 내부클래스가 아닌 외부클래스로 구현
public class RewardListDTO {
    private String name; // -> ItemDTO
    private Integer amount; // -> Reward
    private List<FundingDTO> funding; // RewardListDTO 내부에 있는 FundingDTO 리스트
} 