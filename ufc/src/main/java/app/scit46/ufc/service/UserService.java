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
    // 유저생성(회원가입)
    public UserDTO createUser(UserDTO userDTO) {
        UserEntity userEntity = UserEntity.toEntity(userDTO);
        userRepository.save(userEntity);
        return UserDTO.toDTO(userEntity);
    }

    // 유저조회(회원정보조회)
    public UserDTO readUserById(String userId) throws DBNotFoundException {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new DBNotFoundException("User not found for Read"));
        return UserDTO.toDTO(userEntity);
    }

    // 유저수정(회원정보수정)
    public UserDTO updateUser(UserDTO userDTO) throws DBNotFoundException {
        userRepository.findById(userDTO.getUserId()).orElseThrow(() -> new DBNotFoundException("User not found for Update"));
        UserEntity userEntity = UserEntity.toEntity(userDTO);
        userRepository.save(userEntity);
        return UserDTO.toDTO(userEntity);
    }

    // 유저삭제(회원탈퇴)
    public void deleteUser(String userId) throws DBNotFoundException {
        userRepository.findById(userId).orElseThrow(() -> new DBNotFoundException("User not found for Delete"));
        userRepository.deleteById(userId);
    }
// ------------------ CRUD ------------------ //End



}
