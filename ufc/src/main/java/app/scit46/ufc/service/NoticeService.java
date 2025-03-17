package app.scit46.ufc.service;

import app.scit46.ufc.dto.NoticeDTO;
import app.scit46.ufc.entity.NoticeEntity;
import app.scit46.ufc.repository.NoticeRepository;
import app.scit46.ufc.service.alert.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final AlertService alertService;

    // ✅ 공지사항 목록 조회
    public List<NoticeDTO> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(NoticeDTO::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 공지사항 생성
    public NoticeDTO createNotice(NoticeDTO noticeDTO) {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setTitle(noticeDTO.getTitle());
        noticeEntity.setContent(noticeDTO.getContent());
        noticeEntity.setNoticedDate(LocalDateTime.now());

        NoticeEntity savedNotice = noticeRepository.save(noticeEntity);
        alertService.registAlert(savedNotice, "Notice");
        return NoticeDTO.toDTO(savedNotice);
    }
}
