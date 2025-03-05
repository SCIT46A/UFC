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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
// Jackson 직렬화 문제로 내부클래스가 아닌 외부클래스로 구현
public class RewardListDTO {
    private String name; // -> ItemDTO
    private Integer amount; // -> Reward
    private List<RewardFundingDTO> funding; // RewardListDTO 내부에 있는 FundingDTO 리스트
    private List<RewardFundingDTO> reward; // RewardListDTO 내부에 있는 FundingDTO 리스트
} 