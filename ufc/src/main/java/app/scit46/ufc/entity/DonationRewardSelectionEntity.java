package app.scit46.ufc.entity;

import app.scit46.ufc.dto.DonationRewardSelectionDTO;
import app.scit46.ufc.entity.reward.RewardEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DonationRewardSelections")
public class DonationRewardSelectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selection_id")
    private Long selectionId;

    // 기부 내역과 연결 (MaterialsDonations 테이블)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    private MaterialDonationEntity donation;

    // 선택한 리워드와 연결 (Rewards 테이블)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private RewardEntity reward;

    public static DonationRewardSelectionEntity toEntity(DonationRewardSelectionDTO donationRewardSelectionDTO) {
        return DonationRewardSelectionEntity.builder()
                .selectionId(donationRewardSelectionDTO.getSelectionId())
                .donation(MaterialDonationEntity.toEntity(donationRewardSelectionDTO.getDonation()))
                .reward(RewardEntity.toEntity(donationRewardSelectionDTO.getReward()))
                .build();
    }
}
