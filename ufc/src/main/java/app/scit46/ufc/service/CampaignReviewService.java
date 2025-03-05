package app.scit46.ufc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.campaign.CampaignReviewDTO;
import app.scit46.ufc.entity.campaign.CampaignReviewEntity;
import app.scit46.ufc.repository.CampaignReviewRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignReviewService {
    private final CampaignReviewRepository campaignReviewRepository;

    public List<CampaignReviewDTO> getCampaignReviewsByUserId(Long userId) {
        List<CampaignReviewEntity> temp = campaignReviewRepository.findByReviewedBy_UserId(userId);

        return temp.stream()
                .map(CampaignReviewDTO::toDTO)
                .filter(dto -> dto.getStatus() == true)
                .collect(Collectors.toList());
    }
    
    public Page<CampaignReviewEntity> getListByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return campaignReviewRepository.findByReviewedBy_UserId(userId, pageable);
    }
    
    public long getReviewCountByUserId(Long userId) {
        return campaignReviewRepository.countByReviewedBy_UserId(userId);
    }

    @Transactional
    public void delete(Long cReviewId) {
        CampaignReviewEntity campaignReview = campaignReviewRepository.findById(cReviewId)
                .orElseThrow(() -> new RuntimeException("후기를 찾을 수 없습니다."));
        campaignReview.setStatus(false);
        campaignReviewRepository.save(campaignReview);
    }

    // 후기 페이징 조회
    public Page<CampaignReviewEntity> getList(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("cReviewId"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return campaignReviewRepository.findAll(pageable);
    }
}
