package app.scit46.ufc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.MaterialDonationRepository;
import app.scit46.ufc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import app.scit46.ufc.dto.MaterialDonationDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

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

    public List<MaterialDonationDTO> getDonationsByCampaignIds(List<Long> campaignIds) {
        return materialDonationRepository.findByCampaign_CampaignIdIn(campaignIds)
                .stream()
                .map(MaterialDonationDTO::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🔹 특정 창작자의 캠페인에 대한 기부 내역 조회 (최대 limit 개)
     */
    public List<MaterialDonationDTO> getDonationsByCampaignIds(List<Long> campaignIds, int limit) {
        return materialDonationRepository.findByCampaign_CampaignIdIn(campaignIds, PageRequest.of(0, limit))
                .stream()
                .map(MaterialDonationDTO::toDTO)
                .toList();
    }

    @Transactional
    public void updateDonationStatus(Long donationId, boolean isApproved) {
        try {
            MaterialDonationEntity donation = materialDonationRepository.findById(donationId)
                    .orElseThrow(() -> new RuntimeException("Material Donation not found"));

            donation.setStatus(isApproved ? "approved" : "rejected");
            materialDonationRepository.save(donation);

            System.out.println("✅ DB 저장 완료: " + donation.getDonationId() + ", 상태: " + donation.getStatus());
        } catch (Exception e) {
            System.err.println("❌ DB 업데이트 실패: " + e.getMessage());
            throw e; // 예외를 다시 던져서 트랜잭션이 롤백되는지 확인
        }
    }

}