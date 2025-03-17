package app.scit46.ufc.entity.chat;

import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.chat.ChatRoomDTO;
import app.scit46.ufc.entity.ImageUrlEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.campaign.CampaignBoardEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "ChatRoom")
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 첫 번째 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private UserEntity user1;

    // 두 번째 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private UserEntity user2;

    @Column(name = "created_time")
    @CreationTimestamp
    private LocalDateTime createdTime;



    // DTO -> Entity 변환 메서드
    public static ChatRoomEntity toEntity(ChatRoomDTO dto) {
        return ChatRoomEntity.builder()
                // id는 새로 생성될 때는 생략(자동 생성)
                .user1(UserEntity.toEntity(dto.getUser1()))
                .user2(UserEntity.toEntity(dto.getUser2()))
                // createdTime은 자동 할당되므로 dto에서 따로 셋팅하지 않아도 됩니다.
                .build();
    }
}
