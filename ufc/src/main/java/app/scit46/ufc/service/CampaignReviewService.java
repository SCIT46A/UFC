package app.scit46.ufc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.CampaignReviewDTO;
import app.scit46.ufc.entity.CampaignReviewEntity;
import app.scit46.ufc.repository.CampaignReviewRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignReviewService {
    private final CampaignReviewRepository campaignReviewRepository;

    public List<CampaignReviewDTO> getCampaignReviewsByUserId(Long userId) {
        List<CampaignReviewEntity> temp = campaignReviewRepository.findByReviewedBy_UserId(userId);
        // List<CampaignReviewDTO> list = new ArrayList<>();
        // temp.forEach((entity) -> list.add(CampaignReviewDTO.toDTO(entity)));
        // return list;
        return temp.stream()
            .map(CampaignReviewDTO::toDTO)
            .collect(Collectors.toList());
    }

    public long getReviewCountByUserId(Long userId) {
        return campaignReviewRepository.countByReviewedBy_UserId(userId);
    }
    
}
