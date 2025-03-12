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

    public List<UserAlertDTO> getUserAlertList(Long userId) {
        return userAlertRepository.findByUser_UserIdOrderByCreatedTimeDesc(userId).stream().map(UserAlertDTO::toDTO).collect(Collectors.toList());
    }

    // 작성된 알림과 사용자 아이디를 받아서 알림 전송
    public void sendAlert(Long userId, String alertType, AlertEntity data) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("알림을 받을 사용자가 존재하지 않습니다 UID:"+userId));

        userAlertRepository.save(UserAlertEntity.builder()
            .user(user)
            .alert(data)
            .build());
        
    }

    

}
