package app.scit46.ufc.dto.chat;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.chat.ChatRoomEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChatRoomDTO {

    private Long id;
    private UserDTO user1;
    private UserDTO user2;
    private LocalDateTime createdTime;
    // 보여주기용 creator 객체
    private CreatorDTO creator;

    // 기존 생성자 (UserDTO 버전)
    public ChatRoomDTO(Long id, LocalDateTime createdTime, UserDTO user1, UserDTO user2, CreatorDTO creator) {
        this.id = id;
        this.createdTime = createdTime;
        this.user1 = user1;
        this.user2 = user2;
        this.creator = creator;
    }

    /**
     * JPQL 생성자 표현식용 생성자.
     * 파라미터:
     *  - id, createdTime
     *  - user1, user2: UserEntity 타입 (나중에 UserDTO로 변환)
     *  - creatorId, creatorBName, creatorProImgUrl: creator 정보를 구성하는 스칼라 값들
     *    → 상대방이 ROLE_CREATOR일 경우 해당 값들을 가져오며, 값이 없으면 null이 전달됩니다.
     */
    public ChatRoomDTO(Long id, LocalDateTime createdTime, UserEntity user1, UserEntity user2,
                       Long creatorId, String creatorBName, String creatorProImgUrl) {
        this.id = id;
        this.createdTime = createdTime;
        this.user1 = UserDTO.toDTO(user1);
        this.user2 = UserDTO.toDTO(user2);
        // creatorId와 creatorBName만 있으면 CreatorDTO를 생성합니다.
        if (creatorId != null && creatorBName != null) {
            this.creator = new CreatorDTO(creatorId, creatorBName, creatorProImgUrl);
        } else {
            this.creator = null;
        }
    }

    public static ChatRoomDTO toDTO(ChatRoomEntity entity) {
        return ChatRoomDTO.builder()
                .id(entity.getId())
                .user1(UserDTO.toDTO(entity.getUser1()))
                .user2(UserDTO.toDTO(entity.getUser2()))
                .createdTime(entity.getCreatedTime())
                .build();
    }
}
