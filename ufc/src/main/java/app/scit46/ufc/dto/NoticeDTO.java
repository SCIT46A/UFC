package app.scit46.ufc.dto;

import app.scit46.ufc.entity.NoticeEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class NoticeDTO {

    private Integer noticeId;
    private String title;
    private String content;
    private LocalDateTime noticedDate;

    public static NoticeDTO toDTO(NoticeEntity entity) {
        return NoticeDTO.builder()
                .noticeId(entity.getNoticeId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .noticedDate(entity.getNoticedDate())
                .build();
    }
}
