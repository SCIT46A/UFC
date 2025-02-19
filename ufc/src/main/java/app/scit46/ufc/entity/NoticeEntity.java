package app.scit46.ufc.entity;

import app.scit46.ufc.dto.NoticeDTO;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "noticed_date", nullable = false)
    private LocalDateTime noticedDate;

    // OneToMany: AlertTarget.target_notice 참조
    @OneToMany(mappedBy = "targetNotice", fetch = FetchType.LAZY)
    private List<AlertTargetEntity> alertTargets;

    public static NoticeEntity toEntity(NoticeDTO dto) {
        return builder()
                .noticeId(dto.getNoticeId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .noticedDate(dto.getNoticedDate())
                .build();
    }
}
