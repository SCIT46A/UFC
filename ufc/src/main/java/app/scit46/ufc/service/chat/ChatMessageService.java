package app.scit46.ufc.service.chat;

import app.scit46.ufc.dto.chat.ChatMessageDTO;
import app.scit46.ufc.entity.chat.ChatMessageEntity;
import app.scit46.ufc.repository.chat.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessageDTO> findMessagesByChatRoomId(Long chatRoomId) {
        List<ChatMessageEntity> messageEntities = chatMessageRepository.findByChatRoomIdOrderByCreatedTimeAsc(chatRoomId);

        // 조회된 메시지가 없으면 빈 리스트를 반환
        if(messageEntities == null || messageEntities.isEmpty()){
            return new ArrayList<>();
        }

        return messageEntities.stream()
                .map(ChatMessageDTO::toDTO)
                .collect(Collectors.toList());
    }
}
