package app.scit46.ufc.repository.chat;

import app.scit46.ufc.dto.chat.ChatRoomDTO;
import app.scit46.ufc.entity.chat.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    @Query("SELECT new app.scit46.ufc.dto.chat.ChatRoomDTO(" +
            "c.id, " +
            "c.createdTime, " +
            "u1, " +
            "u2, " +
            "CASE WHEN u1.userId = :myUserId THEN cr2.creatorId " +
            "     WHEN u2.userId = :myUserId THEN cr1.creatorId " +
            "     ELSE null END, " +
            "CASE WHEN u1.userId = :myUserId THEN cr2.bName " +
            "     WHEN u2.userId = :myUserId THEN cr1.bName " +
            "     ELSE null END, " +
            "CASE WHEN u1.userId = :myUserId THEN cp2.imageId " +
            "     WHEN u2.userId = :myUserId THEN cp1.imageId " +
            "     ELSE null END) " +
            "FROM ChatRoomEntity c " +
            "JOIN c.user1 u1 " +
            "JOIN c.user2 u2 " +
            "LEFT JOIN u1.creators cr1 " +
            "LEFT JOIN u2.creators cr2 " +
            "LEFT JOIN cr1.proImgUrl cp1 " +
            "LEFT JOIN cr2.proImgUrl cp2 " +
            "WHERE u1.userId = :myUserId OR u2.userId = :myUserId")
    List<ChatRoomDTO> findAllWithCreator(@Param("myUserId") Long myUserId);

    @Query("SELECT cr FROM ChatRoomEntity cr WHERE " +
            "(cr.user1.userId = :user1 AND cr.user2.userId = :user2) " +
            "OR (cr.user1.userId = :user2 AND cr.user2.userId = :user1)")
    Optional<ChatRoomEntity> findChatRoomByUsers(@Param("user1") Long user1, @Param("user2") Long user2);



}

