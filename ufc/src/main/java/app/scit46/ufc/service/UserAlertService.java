package app.scit46.ufc.service;

import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.UserAlertRepository;
import app.scit46.ufc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAlertService {

    @Autowired
    private UserAlertRepository userAlertRepository;

    @Autowired
    private UserRepository userRepository; // UserEntity 조회를 위해 필요

    public boolean alertCheck(Long userId) {
        if (userId == null) {
            return false; // userId가 없으면 알람 확인 불가능
        }

        // userId를 기반으로 UserEntity 조회
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false; // 해당 userId를 가진 사용자가 없으면 false 반환
        }

        // is_read = false(읽지 않은 알람) 여부 확인
        return userAlertRepository.existsByUserAndIsRead(user, false);
    }


}
