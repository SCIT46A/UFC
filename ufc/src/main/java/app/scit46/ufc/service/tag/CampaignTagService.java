package app.scit46.ufc.service.tag;

import app.scit46.ufc.dto.campaign.CampaignTagDTO;
import app.scit46.ufc.entity.campaign.CampaignTagEntity;
import app.scit46.ufc.repository.tag.CampaignTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignTagService {

    private final CampaignTagRepository campaignTagRepository;

    public List<CampaignTagDTO> findTagsByCampaignId(Long campaignId) {
        List<CampaignTagEntity> tagEntities = campaignTagRepository.findTagsByCampaign_CampaignId(campaignId);
        return tagEntities.stream()
                .map(CampaignTagDTO::toDTO) // 기존 DTO 변환 유지
                .collect(Collectors.toList());
    }

}