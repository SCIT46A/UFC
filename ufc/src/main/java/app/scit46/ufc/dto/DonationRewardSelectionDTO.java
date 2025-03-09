package app.scit46.ufc.dto;

import app.scit46.ufc.dto.reward.RewardDTO;
import app.scit46.ufc.entity.DonationRewardSelectionEntity;
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
@Builder
@ToString
public class DonationRewardSelectionDTO {

    private Long selectionId;
    private MaterialDonationDTO donation;
    private RewardDTO reward;

    public static DonationRewardSelectionDTO toDTO(DonationRewardSelectionEntity donationRewardSelection) {
        return DonationRewardSelectionDTO.builder()
                .selectionId(donationRewardSelection.getSelectionId())
                .donation(MaterialDonationDTO.toDTO(donationRewardSelection.getDonation()))
                .reward(RewardDTO.toDTO(donationRewardSelection.getReward()))
                .build();
    }
}
