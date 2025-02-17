package app.scit46.ufc.service;

import app.scit46.ufc.dto.CampaignDTO;
import app.scit46.ufc.entity.CampaignEntity;
import app.scit46.ufc.repository.CampaignRepository;
import app.scit46.ufc.util.HangulUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;


//  검색창 입력했을때 밑에 나오는거


//  현재 시간 기준 진행중인 캠페인들
    public List<CampaignDTO> getAllCampaigns() {

        List<CampaignDTO> campaigns = campaignRepository.findAll()
                .stream() // 필터 적용
                .map(CampaignDTO::toDTO) // DTO 변환
                .collect(Collectors.toList()); // 리스트로 변환
        return campaigns;
    }



}
