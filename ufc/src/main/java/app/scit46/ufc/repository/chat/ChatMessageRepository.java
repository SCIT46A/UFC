package app.scit46.ufc.repository.chat;

import app.scit46.ufc.entity.chat.ChatMessageEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByChatRoomId(Long chatRoomId);
    List<ChatMessageEntity> findByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long id);
    List<ChatMessageEntity> findByChatRoomIdOrderByCreatedTimeAsc(Long chatRoomId);
}
