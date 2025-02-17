package app.scit46.ufc.service;

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
    

    public MaterialDonationEntity donationFindByUserId(Long userId) {
    UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return materialDonationRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Material Donation not found"));
    }
}
