package app.scit46.ufc.service;

import app.scit46.ufc.dto.NoticeDTO;
import app.scit46.ufc.entity.NoticeEntity;
import app.scit46.ufc.repository.NoticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

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
        noticeEntity.setNoticedDate(noticeDTO.getNoticedDate());

        NoticeEntity savedNotice = noticeRepository.save(noticeEntity);
        return NoticeDTO.toDTO(savedNotice);
    }
}
