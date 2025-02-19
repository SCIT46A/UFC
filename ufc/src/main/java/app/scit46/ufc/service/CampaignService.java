package app.scit46.ufc.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.CampaignDTO;
import app.scit46.ufc.dto.CampaignGoalDTO;
import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.entity.CampaignEntity;
import app.scit46.ufc.repository.CampaignGoalRepository;
import app.scit46.ufc.repository.CampaignRepository;
import app.scit46.ufc.repository.MaterialDonationRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignGoalRepository campaignGoalRepository;
    private final MaterialDonationRepository materialDonationRepository;

    public List<CampaignEntity> campaignFindByCampaignId(Long campaignId) {
      return campaignRepository.findByCampaignId(campaignId);
    }

    //펀딩 대기 중인
    public List<CampaignDTO> getFundingWaitingCampaigns() {
        LocalDateTime now = LocalDateTime.now();
        return campaignRepository.findByCampaignStatusAndStartDateAfter(1, now)
                .stream()
                .map(CampaignDTO::toDTO) // ✅ modelMapper 대신 직접 변환
                .collect(Collectors.toList());
    }



    // ✅ 승인 대기 중인 캠페인 조회 (N+1 문제 해결)
    @Transactional(readOnly = true)
    public List<CampaignDTO> getPendingCampaigns() {
        return campaignRepository.findByPendingApproval().stream()
                .map(CampaignDTO::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 개별 캠페인 승인 (Dirty Checking 활용)
    @Transactional
    public void approveCampaign(Long campaignId) {
        CampaignEntity campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("캠페인을 찾을 수 없습니다."));
        campaign.setCampaignStatus(1);
        campaignRepository.save(campaign); // ✅ 변경 사항 저장 필요!
    }


    // ✅ 여러 개의 캠페인 승인 (일괄 처리 추가)
    @Transactional
    public void approveMultipleCampaigns(List<Long> campaignIds) {
        List<CampaignEntity> campaigns = campaignRepository.findAllById(campaignIds);

        if (campaigns.isEmpty()) {
            throw new RuntimeException("선택된 캠페인을 찾을 수 없습니다.");
        }

        for (CampaignEntity campaign : campaigns) {
            campaign.setCampaignStatus(1);
        }

        campaignRepository.saveAll(campaigns);
    }

    // ✅ 전체 캠페인 조회 (N+1 문제 해결)
    @Transactional(readOnly = true)
    public List<CampaignDTO> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .map(CampaignDTO::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 캠페인 목표 조회 (CampaignGoalEntity → CampaignGoalDTO 변환)
    @Transactional(readOnly = true)
    public List<CampaignGoalDTO> getAllCampaignGoals() {
        return campaignGoalRepository.findAll().stream()
                .map(CampaignGoalDTO::toDTO)  // Entity → DTO 변환
                .collect(Collectors.toList());
    }

    // ✅ 캠페인 기부 내역 조회 (MaterialDonationEntity → MaterialDonationDTO 변환)
    @Transactional(readOnly = true)
    public List<MaterialDonationDTO> getAllMaterialDonations() {
        return materialDonationRepository.findAll().stream()
                .map(MaterialDonationDTO::toDTO)  // Entity → DTO 변환
                .collect(Collectors.toList());
    }
}
