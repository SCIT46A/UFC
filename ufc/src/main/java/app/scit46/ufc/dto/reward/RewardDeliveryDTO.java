package app.scit46.ufc.dto.reward;

import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RewardDeliveryDTO {
    private Long rDeliveryId;
    private String invoice;
    private String status;
    private Long rewardId; // ✅ rewardId 추가
    private MaterialDonationDTO donation; // ✅ MaterialDonationDTO 유지
    private Integer amount;

    public static RewardDeliveryDTO toDTO(RewardDeliveryEntity entity) {
        return RewardDeliveryDTO.builder()
                .rDeliveryId(entity.getRDeliveryId())
                .invoice(entity.getInvoice())
                .status(entity.getStatus()) // ✅ Enum 타입으로 유지
                .rewardId(entity.getReward() != null ? entity.getReward().getRewardId() : null) // ✅ RewardEntity 변환
                .donation(entity.getDonation() != null ? MaterialDonationDTO.toDTO(entity.getDonation()) : null) // ✅
                                                                                                                 // MaterialDonationDTO
                                                                                                                 // 변환
                .amount(entity.getAmount())
                .build();
    }
}
