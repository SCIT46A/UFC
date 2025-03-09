package app.scit46.ufc.service.reward;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.DonationRewardSelectionDTO;
import app.scit46.ufc.entity.DonationRewardSelectionEntity;
import app.scit46.ufc.repository.reward.DonationRewardSelectionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DonationRewardSelectionService {

    private final DonationRewardSelectionRepository donationRewardSelectionRepository;

    public List<DonationRewardSelectionDTO> getDonationRewardSelectionsByUserId(Long userId) {
        List<DonationRewardSelectionEntity> donationRewardSelections = donationRewardSelectionRepository.findByDonationUserId(userId);
        return donationRewardSelections.stream()
                .map(DonationRewardSelectionDTO::toDTO)
                .collect(Collectors.toList());
    }
}