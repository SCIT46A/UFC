package app.scit46.ufc.service.chat;

import app.scit46.ufc.dto.chat.ChatRoomDTO;
import app.scit46.ufc.entity.chat.ChatRoomEntity;
import app.scit46.ufc.repository.chat.ChatRoomRepository;
import app.scit46.ufc.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;

//    public List<ChatRoomDTO> findAll(Long userId) {
//        List<ChatRoomEntity> chatRooms = chatRoomRepository.findByUser1UserIdOrUser2UserId(userId, userId);
//        // 엔티티를 DTO로 변환하는 로직 (예: ModelMapper나 수동 변환)
//        return chatRooms.stream()
//                .map(ChatRoomDTO::toDTO)
//                .collect(Collectors.toList());
//    }

    public List<ChatRoomDTO> findAll(Long userId) {
        // userId가 null인 경우에 대한 처리(예: 예외 발생)를 추가할 수도 있습니다.
        if (userId == null) {
            return null;
        }
        return chatRoomRepository.findAllWithCreator(userId);
    }

    @Transactional
    public ChatRoomDTO createChatRoom(Long loginUserId, Long userId) {
        // 두 사용자의 채팅방 존재 여부 확인 (순서 무관)
        Optional<ChatRoomEntity> existing = chatRoomRepository.findChatRoomByUsers(loginUserId, userId);
        if(existing.isPresent()){
            return ChatRoomDTO.toDTO(existing.get());
        }
        // 새 채팅방 생성
        ChatRoomEntity newRoom = ChatRoomEntity.builder()
                .user1(userService.findById(loginUserId))
                .user2(userService.findById(userId))
                .build();
        newRoom = chatRoomRepository.save(newRoom);
        return ChatRoomDTO.toDTO(newRoom);
    }
}
