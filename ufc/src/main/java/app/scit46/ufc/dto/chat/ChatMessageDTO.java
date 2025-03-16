package app.scit46.ufc.dto.chat;

import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.entity.chat.ChatMessageEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long id;
    private ChatRoomDTO chatRoomId;
    private UserDTO senderId;
    private String content;
    private LocalDateTime createdTime;

    public static ChatMessageDTO toDTO(ChatMessageEntity entity) {
        return ChatMessageDTO.builder()
                .id(entity.getId())
                // ChatRoomDTO로 변환
                .chatRoomId(ChatRoomDTO.toDTO(entity.getChatRoom()))
                // UserDTO로 변환 (발신자)
                .senderId(UserDTO.toDTO(entity.getSender()))
                .content(entity.getContent())
                .createdTime(entity.getCreatedTime())
                .build();
    }
}
