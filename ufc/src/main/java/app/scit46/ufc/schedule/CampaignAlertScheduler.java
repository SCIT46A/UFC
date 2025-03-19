package app.scit46.ufc.schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import app.scit46.ufc.dto.alert.AlertDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.entity.alert.AlertEntity;
import app.scit46.ufc.exception.handler.DBExceptionHandler;
import app.scit46.ufc.repository.UserRepository;
import app.scit46.ufc.repository.campaign.CampaignRepository;
import app.scit46.ufc.service.OAuth2UserService;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.alert.AlertService;
import app.scit46.ufc.service.campaign.CampaignService;
import app.scit46.ufc.service.product.PayService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CampaignAlertScheduler {

    private final CampaignService campaignService;
    private final CampaignRepository campaignRepository;
    private final AlertService alertService;
    private final UserService userService;


    /*
     * 캠페인이 시작됨 -> 날짜 Schedule, 캠페인 찜한 사람 대상(창작자 찜은 제외?)
     * Campaigns.start_date > now() -> add UserAlerts (Alert는 캠페인 승인시 미리 생성 필요)
     */
    @Scheduled(fixedRate = 60000 * 60 * 24)
    public void checkAndStartCampaigns() {
        // LocalDateTime now = LocalDateTime.now();
        // List<CampaignDTO> campaigns = campaignRepository.findByStartDateAfter(LocalDateTime.now())
        //                             .stream().map(CampaignDTO::toDTO).collect(Collectors.toList());//campaignService.getReadyCampaigns(now);

        // for (CampaignDTO campaign : campaigns) {
        //     alertRepository.
        //     if(true){
        //         // 이미 알림이 전송된(알림이 등록된) 것이면 탈출
        //     }else{
        //         campaignService.startCampaign(campaign);

        //         // 조건 만족시 알림 생성
        //         AlertEntity startAlert = alertService.registAlert(campaign, "cStart");

        //         // UserAlerts 생성
        //         List<Long> userIds = userService.getUsersByCampaign(campaign.getId());
        //         alertService.createUserAlerts(startAlert.getId(), userIds);

        //         System.out.println("🚀 캠페인 시작: " + campaign.getName());
        //     }
            
        // }
    }

    /*
     * 캠페인 종료일이 임박했을 때(1/3일 전) -> 날짜 Schedule, 캠페인 찜, 펀딩 참여자
     * Campaigns.end_date - now() in (1, 3)days -> add UserAlert(Alert는 캠페인 승인시 미리
     * 생성 필요) where 캠페인 id 조회 -> LikeEntity의 campaign / MaterialDonationEntity의
     * campaign
     */
    @Scheduled(fixedRate = 60000 * 60 * 24)
    public void checkAndEndCampaigns(){

    }

    /*
     * 캠페인 리워드 배송 마감일 -> 날짜 Schedule, 창작자에게 지정한 리워드 발송 마감일 예고(D-7부터?)
     * Campaigns.send_date - now() between(0,7) -> add UserAlert(Alert는 캠페인 승인시 미리
     * 생성 필요, 해당 캠페인을 생성한 Creator에게 전송)
     */
    @Scheduled(fixedRate = 60000 * 60 * 24)
    public void checkAndRewardSendCampaigns(){

    }

    /*
     * 캠페인 달성률이 100%를 넘었을 때 -> RewardMaterials, MaterialDonations 간의 차이 계산 Schedule,
     * 캠페인 찜, 펀딩 참여자
     * Campaigns.campaign_id | -> reward -> rewardMaterials / -> materialDonations
     * RewardMaterials.material.material_id/RewardMaterials.quantity_required,
     * MaterialDonations.material.id/MaterialDonations.quantity의 합 간의 차이 <= 0
     * 캠페인 id 조회 -> LikeEntity의 campaign / MaterialDonationEntity의 campaign
     */
    @Scheduled(fixedRate = 60000 * 60 * 24)
    public void checkAndCompleteCampaigns(){

    }
}