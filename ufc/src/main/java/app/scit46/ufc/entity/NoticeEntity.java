package app.scit46.ufc.entity;

import app.scit46.ufc.dto.NoticeDTO;
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
@Table(name = "Notices")
public class NoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Integer noticeId;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "content", nullable = false, unique = true, length = 200)
    private String content;

    @CreationTimestamp
    @Column(name = "noticed_date", nullable = false)
    private LocalDateTime noticedDate;

    public static NoticeEntity toEntity(NoticeDTO dto) {
        return builder()
                .noticeId(dto.getNoticeId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .noticedDate(dto.getNoticedDate())
                .build();
    }
}
