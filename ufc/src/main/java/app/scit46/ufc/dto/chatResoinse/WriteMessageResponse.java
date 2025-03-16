package app.scit46.ufc.dto.chatResoinse;

import app.scit46.ufc.entity.chat.ChatMessageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

// 예시: WriteMessageResponse DTO (순환 참조 없는 버전)
@Data
@AllArgsConstructor
public class WriteMessageResponse {
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private LocalDateTime createdTime;

    public WriteMessageResponse(ChatMessageEntity cm) {
        this.id = cm.getId();
        this.chatRoomId = cm.getChatRoom().getId();
        this.senderId = cm.getSender().getUserId();
        this.content = cm.getContent();
        this.createdTime = cm.getCreatedTime();
    }
}
