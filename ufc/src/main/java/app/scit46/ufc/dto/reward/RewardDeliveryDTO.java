package app.scit46.ufc.dto.reward;

import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.dto.reward.RewardDTO;
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
    // 캠페인 대신 리워드 정보를 포함하도록 수정
    private RewardDTO reward;
    private MaterialDonationDTO donation;
    // 추가된 amount 필드
    private Integer amount;

    public static RewardDeliveryDTO toDTO(RewardDeliveryEntity entity) {
        if (entity == null)
            return null;
        return RewardDeliveryDTO.builder()
                .rDeliveryId(entity.getRDeliveryId())
                .invoice(entity.getInvoice())
                .status(entity.getStatus())
                .reward(entity.getReward() != null ? RewardDTO.toDTOMinimal(entity.getReward()) : null)
                .donation(entity.getDonation() != null ? MaterialDonationDTO.toDTOMinimal(entity.getDonation()) : null)
                .amount(entity.getAmount())
                .build();
    }
}
