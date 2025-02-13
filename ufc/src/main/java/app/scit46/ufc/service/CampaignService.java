package app.scit46.ufc.service;

import app.scit46.ufc.dto.CampaignDTO;
import app.scit46.ufc.dto.CampaignGoalDTO;
import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.entity.CampaignEntity;
import app.scit46.ufc.entity.CampaignGoalEntity;
import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.repository.CampaignRepository;
import app.scit46.ufc.repository.CampaignGoalRepository;
import app.scit46.ufc.repository.MaterialDonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final CampaignGoalRepository campaignGoalRepository;
    private final MaterialDonationRepository materialDonationRepository;

    // ✅ 생성자 주입 (의존성 주입)
    public CampaignService(CampaignRepository campaignRepository,
                           CampaignGoalRepository campaignGoalRepository,
                           MaterialDonationRepository materialDonationRepository) {
        this.campaignRepository = campaignRepository;
        this.campaignGoalRepository = campaignGoalRepository;
        this.materialDonationRepository = materialDonationRepository;
    }

    // ✅ 캠페인 목표 조회
    @Transactional(readOnly = true)
    public List<CampaignGoalDTO> getAllCampaignGoals() {
        List<CampaignGoalEntity> goals = campaignGoalRepository.findAll();
        return goals.stream().map(CampaignGoalDTO::toDTO).collect(Collectors.toList());
    }

    // ✅ 캠페인 기부 내역 조회
    @Transactional(readOnly = true)
    public List<MaterialDonationDTO> getAllMaterialDonations() {
        List<MaterialDonationEntity> donations = materialDonationRepository.findAll();
        return donations.stream().map(MaterialDonationDTO::toDTO).collect(Collectors.toList());
    }

    // ✅ 전체 캠페인 조회 (N+1 문제 해결)
    @Transactional(readOnly = true)
    public List<CampaignDTO> getAllCampaigns() {
        return campaignRepository.findAllWithCreator().stream()
                .map(CampaignDTO::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 승인 대기 중인 캠페인 조회 (N+1 문제 해결)
    @Transactional(readOnly = true)
    public List<CampaignDTO> getPendingCampaigns() {
        return campaignRepository.findByPendingApproval().stream()
                .map(CampaignDTO::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 캠페인 승인 처리 (Dirty Checking 활용)
    @Transactional
    public void approveCampaign(Long campaignId) {
        CampaignEntity campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("캠페인을 찾을 수 없습니다."));
        campaign.setCampaignStatus(true);  // ✅ 승인 처리 (Dirty Checking)
    }
}
