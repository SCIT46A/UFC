package app.scit46.ufc.dto.chatResoinse;

import app.scit46.ufc.entity.chat.ChatMessageEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MessagesResponse {
    private List<ChatMessageEntity> chatMessages;
    private int totalCount;
}
