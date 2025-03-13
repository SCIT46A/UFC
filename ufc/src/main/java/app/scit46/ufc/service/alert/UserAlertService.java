package app.scit46.ufc.service.alert;

import app.scit46.ufc.dto.alert.UserAlertDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.alert.AlertEntity;
import app.scit46.ufc.entity.alert.UserAlertEntity;
import app.scit46.ufc.repository.UserRepository;
import app.scit46.ufc.repository.alert.UserAlertRepository;
import app.scit46.ufc.service.LikeService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAlertService {

    private final LikeService likeService;
    private final UserAlertRepository userAlertRepository;
    private final UserRepository userRepository; // UserEntity 조회를 위해 필요

    public List<UserAlertDTO> alertCheck(Long userId) {

        // userId를 기반으로 UserEntity 조회
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        // is_read = false(읽지 않은 알람) 여부 확인
        return userAlertRepository.findByUserAndReadTimeIsNull(user).stream().map(UserAlertDTO::toDTO).collect(Collectors.toList());
    }

    // 사용자가 받은 알림 조회
    public List<UserAlertDTO> getUserAlertList(Long userId) {
        return userAlertRepository.findByUser_UserIdOrderByCreatedTimeDesc(userId).stream().map(UserAlertDTO::toDTO).collect(Collectors.toList());
    }

    // 작성된 알림과 사용자 아이디를 받아서 알림 전송
    public void sendAlert(String alertType, AlertEntity alert, List<UserEntity> users) {
        switch(alertType){
            case "Notice":
                break;
            case "Campaign":
                break;
            case "CampaignBoard":
                break;
            case "Badge":
                break;
            case "MaterialDonation":
                break;
            case "Product":
                break;
            case "RewardDelivery":
                break;
            case "ProductDelivery":
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 알림 타입입니다.");
        }
        
        // 알림 저장
        saveAlert(alert, users);
    }

    public void saveAlert(AlertEntity alert, List<UserEntity> users){
        for (UserEntity user : users) {
            userAlertRepository.save(UserAlertEntity.builder()
                    .user(user)
                    .alert(alert)
                    .build());
        }
    }

}
