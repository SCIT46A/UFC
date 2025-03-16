package app.scit46.ufc.dto.chatResoinse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WriteMessageRequest {
    private Long chatRoomId;
    private Long senderId;
    private String content;
}
