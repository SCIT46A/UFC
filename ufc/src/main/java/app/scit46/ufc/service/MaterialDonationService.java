package app.scit46.ufc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.repository.MaterialDonationRepository;
import app.scit46.ufc.repository.UserRepository;
import app.scit46.ufc.service.campaign.CampaignService;
import app.scit46.ufc.service.delivery.DeliveryService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialDonationService {
    private final UserRepository userRepository;
    private final MaterialDonationRepository materialDonationRepository;
    private final CourierService courierService;
    private final DeliveryService deliveryService;

    private CampaignService campaignService;


    @Autowired
    public void setCampaignService(@Lazy CampaignService campaignService) {
        this.campaignService = campaignService;
    }



    public List<MaterialDonationDTO> donationFindByUserId(Long userId) {
        List<MaterialDonationEntity> materialDonationEntities = materialDonationRepository.findAllByUser_UserId(userId);
        if (materialDonationEntities.isEmpty()) {
            throw new RuntimeException("Material Donation not found");
        }
        return materialDonationEntities.stream()
                .map(MaterialDonationDTO::toDTO)
                .toList();
    }
    
    public List<MaterialDonationDTO> getMaterialDonationsByUserId(Long userId) {
        List<MaterialDonationEntity> temp = materialDonationRepository.findAllByUser_UserId(userId);
        if (temp.isEmpty()) {
            throw new RuntimeException("Material Donation not found");
        }
        return temp.stream()
                .map(MaterialDonationDTO::toDTO)
                .toList();
    }


    public List<MaterialDonationDTO> findDonationByCampaign(Long campaignId) {
        return materialDonationRepository.findByCampaign_CampaignId(campaignId).stream().map(MaterialDonationDTO::toDTO)
                .collect(Collectors.toList());
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

    public Map<String, Object> getDonationData(List<Long> campaignIds) {
        List<MaterialDonationDTO> donations = getDonationsByCampaignIds(campaignIds);
        Map<String, String> trackingStatuses = deliveryService.trackMultipleDeliveries(donations);

        List<Map<String, Object>> transformedDonations = donations.stream().map(donation -> {
            String courierId = donation.getCourierId();
            String trackingNumber = donation.getTrackingNumber();

            Map<String, Object> donationMap = new HashMap<>();
            donationMap.put("donationId", donation.getDonationId());
            donationMap.put("campaignTitle", donation.getCampaign().getTitle());
            donationMap.put("donatedDate", donation.getDonatedDate());
            donationMap.put("userName", donation.getUser().getUserName());
            donationMap.put("materialName", donation.getMaterial().getName());
            donationMap.put("quantity", donation.getQuantity());
            donationMap.put("trackingNumber", trackingNumber);
            donationMap.put("courierName", courierService.getCourierNameById(courierId));
            String trackingStatus = trackingStatuses.getOrDefault(trackingNumber, "미등록");
            donationMap.put("trackingStatus", trackingStatus);
            donationMap.put("status", donation.getStatus());
            return donationMap;
        }).collect(Collectors.toList());

        Map<String, Long> donationCounts = donations.stream()
                .collect(Collectors.groupingBy(MaterialDonationDTO::getStatus, Collectors.counting()));

        return Map.of(
                "campaigns", campaignService.findByCampaign_CampaignIdIn(campaignIds),
                "donations", transformedDonations,
                "donationCounts", donationCounts);
    }

    public Map<String, Long> getDonationCountsByCampaignIds(List<Long> campaignIds) {
        List<MaterialDonationDTO> donations = getDonationsByCampaignIds(campaignIds);

        Map<String, Long> donationCounts = donations.stream()
                .collect(Collectors.groupingBy(MaterialDonationDTO::getStatus, Collectors.counting()));

        Map<String, Long> completeCounts = new HashMap<>();
        completeCounts.put("pending", donationCounts.getOrDefault("pending", 0L));
        completeCounts.put("processing", donationCounts.getOrDefault("processing", 0L));
        completeCounts.put("rejected", donationCounts.getOrDefault("rejected", 0L));
        completeCounts.put("approved", donationCounts.getOrDefault("approved", 0L));

        return completeCounts;
    }
}