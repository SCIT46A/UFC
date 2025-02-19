package app.scit46.ufc.service;

import app.scit46.ufc.dto.UserAlertDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.UserAlertRepository;
import app.scit46.ufc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAlertService {

    @Autowired
    private UserAlertRepository userAlertRepository;

    @Autowired
    private UserRepository userRepository; // UserEntity 조회를 위해 필요

    public List<UserAlertDTO> alertCheck(Long userId) {

        // userId를 기반으로 UserEntity 조회
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        // is_read = false(읽지 않은 알람) 여부 확인
        return userAlertRepository.findByUserAndIsReadFalse(user).stream().map(UserAlertDTO::toDTO).collect(Collectors.toList());
    }


}
