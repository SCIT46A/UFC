package app.scit46.ufc.service;

import java.util.Optional;

import org.hibernate.query.Page;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

// ------------------ CRUD ------------------


    // 유저조회(회원정보조회)
    public UserDTO readUserById(Long userId) throws DBNotFoundException {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new DBNotFoundException("User not found for Read"));
        return UserDTO.toDTO(userEntity);
    }

    // 회원탈퇴
    @Transactional
    public void delete(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setUserStatus(0);
        userRepository.save(user);
    }
    
    //회원 정보 업데이트
    @Transactional
    public void userUpdate(UserDTO userDTO) {
        System.out.println(userDTO.getUserId());
        Optional<UserEntity> temp = userRepository.findById(userDTO.getUserId());
        if (!temp.isPresent())
            return;
        UserEntity entity = temp.get();
        entity.setUserName(userDTO.getUserName());
        entity.setIntro(userDTO.getIntro());
        entity.setPhoneNumber(userDTO.getPhoneNumber());
        entity.setUserAddress(userDTO.getUserAddress());
    }
    // ------------------ CRUD ------------------ //End

    //유저가 기부한 정보 조회

    // 유저 이름으로 유저 조회 -> 유저 아이디 반환
    public Long findUserIdByUserName(String userName) {
        UserEntity user = userRepository.findByUserName(userName).orElse(null);
        return user.getUserId();
    }

    // OAuth 인증정보로 유저 조회(회원정보조회)
    public UserEntity findUserByIdentity(String identity) {
        return userRepository.findByOauthId(identity).orElse(null);
    }

    public UserEntity saveUser(UserDTO userDTO) {
        UserEntity user = new UserEntity();
        user.setOauthId(userDTO.getOauthId());
        user.setUserName(userDTO.getUserName());
        user.setEmail(userDTO.getEmail());
        user.setLoginType(userDTO.getLoginType()); // 제공자 정보 설정
        user.setRoles("ROLE_USER");
        user.setUserStatus(1);
        user.setIsMarketed(userDTO.getIsMarketed());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setIntro(userDTO.getIntro());
        user.setUserAddress(userDTO.getUserAddress());
        return userRepository.save(user);
    }

    public UserEntity findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }




}
