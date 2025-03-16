package app.scit46.ufc.controller;

import app.scit46.ufc.chat.RsData;
import app.scit46.ufc.dto.chatResoinse.MessagesRequest;
import app.scit46.ufc.dto.chatResoinse.MessagesResponse;
import app.scit46.ufc.dto.chatResoinse.WriteMessageRequest;
import app.scit46.ufc.dto.chatResoinse.WriteMessageResponse;
import app.scit46.ufc.entity.chat.ChatMessageEntity;
import app.scit46.ufc.entity.chat.ChatRoomEntity;
import app.scit46.ufc.repository.chat.ChatMessageRepository;
import app.scit46.ufc.repository.chat.ChatRoomRepository;
import app.scit46.ufc.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;



    // 컨트롤러 내에서
    @PostMapping("/chat/writeMessage")
    @ResponseBody
    public RsData<WriteMessageResponse> writeMessage(@RequestBody WriteMessageRequest writeMessageRequest) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(writeMessageRequest);
            log.info("Received WriteMessageRequest: " + json);
        } catch (Exception e) {
            log.error("Error converting WriteMessageRequest to JSON", e);
        }

        ChatRoomEntity chatRoom = chatRoomRepository.findById(writeMessageRequest.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        ChatMessageEntity cm = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .sender(userService.findById(writeMessageRequest.getSenderId()))
                .content(writeMessageRequest.getContent())
                .build();

        chatMessageRepository.save(cm);

        messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoom.getId(), new WriteMessageResponse(cm));

        return new RsData<>("200", "메세지가 작성되었습니다.", new WriteMessageResponse(cm));
    }


    @GetMapping("/chat/messages")
    @ResponseBody
    public RsData<MessagesResponse> messages(MessagesRequest messagesRequest) {
        List<ChatMessageEntity> messages;
        if (messagesRequest.fromId() == null || messagesRequest.fromId() == 0) {
            messages = chatMessageRepository.findByChatRoomId(messagesRequest.chatRoomId());
        } else {
            messages = chatMessageRepository.findByChatRoomIdAndIdGreaterThan(messagesRequest.chatRoomId(), messagesRequest.fromId());
        }
        return new RsData<>("200", "메세지 가져오기 성공", new MessagesResponse(messages, messages.size()));
    }

    @GetMapping("/room")
    public String room() {
        return "/room";
    }

    @GetMapping("/create")
    public String create() {
        return "/createRoom";
    }
}
