package app.scit46.ufc.service;

import org.springframework.stereotype.Service;

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


// ------------------ CRUD ------------------ //End

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



}
