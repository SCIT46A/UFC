package app.scit46.ufc.entity.chat;

import app.scit46.ufc.dto.chat.ChatMessageDTO;
import app.scit46.ufc.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "ChatMessage")
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 채팅방에 속하는 메시지인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @JsonIgnore  // 이 필드를 JSON 직렬화 시 무시
    private ChatRoomEntity chatRoom;

    // 메시지 발신자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_time", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdTime;

    public static ChatMessageEntity toEntity(ChatMessageDTO dto) {
        return ChatMessageEntity.builder()
                // id는 새로 생성될 때 생략
                // dto의 chatRoomId 필드는 ChatRoomDTO 타입이라고 가정합니다.
                .chatRoom(ChatRoomEntity.toEntity(dto.getChatRoomId()))
                // dto의 senderId 필드는 UserDTO 타입이라고 가정합니다.
                .sender(UserEntity.toEntity(dto.getSenderId()))
                .content(dto.getContent())
                // createdTime은 자동 생성되므로 생략
                .build();
    }
}
