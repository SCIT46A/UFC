package app.scit46.ufc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.repository.MaterialDonationRepository;
import app.scit46.ufc.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialDonationService {
    private final UserRepository userRepository;
    private final MaterialDonationRepository materialDonationRepository;



    public List<MaterialDonationDTO> donathionFindByUserId(Long userId) {
        List<MaterialDonationEntity> materialDonationEntities = materialDonationRepository.findAllByUser_UserId(userId);
        if (materialDonationEntities.isEmpty()) {
            throw new RuntimeException("Material Donation not found");
        }
        return materialDonationEntities.stream()
                .map(MaterialDonationDTO::toDTO)
                .toList();
    }
    

    // public List<MaterialDonationEntity> donationFindByUserId(Long userId) {
    //     UserEntity user = userRepository.findById(userId)
    //             .orElseThrow(() -> new RuntimeException("User not found"));

    //     List<MaterialDonationEntity> donations = materialDonationRepository.findByUser(user);
    //     if (donations.isEmpty()) {
    //         throw new RuntimeException("Material Donation not found");
    //     }
    //     return donations;
    // }
}