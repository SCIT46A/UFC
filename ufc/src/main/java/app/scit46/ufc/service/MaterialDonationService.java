package app.scit46.ufc.service;

import java.util.List;
import java.util.stream.Collectors;

import app.scit46.ufc.dto.MaterialDonationDTO;
import org.springframework.stereotype.Service;

import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.MaterialDonationRepository;
import app.scit46.ufc.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialDonationService {
    private final UserRepository userRepository;
    private final MaterialDonationRepository materialDonationRepository;

    public MaterialDonationEntity findByUserId(Long userId) {
        return materialDonationRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Material Donation not found"));
    }

    public List<MaterialDonationEntity> donationFindByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MaterialDonationEntity> donations = materialDonationRepository.findByUser(user);
        if (donations.isEmpty()) {
            throw new RuntimeException("Material Donation not found");
        }
        return donations;
    }

    public List<MaterialDonationDTO> findDonationByCampaign (Long campaignId) {
        return materialDonationRepository.findByCampaign_CampaignId(campaignId).stream().map(MaterialDonationDTO::toDTO).collect(Collectors.toList());
    }



}