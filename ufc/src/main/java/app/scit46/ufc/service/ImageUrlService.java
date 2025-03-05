package app.scit46.ufc.service;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.entity.ImageUrlEntity;
import app.scit46.ufc.repository.ImageUrlRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageUrlService {

    private final ImageUrlRepository imageUrlRepository;

    public Long getId(String imageId) {
        return imageUrlRepository.findIdByImageId(imageId);
    }

    // CRD
    public void save(ImageUrlDTO imageUrlDTO) {
        imageUrlRepository.save(ImageUrlEntity.toEntity(imageUrlDTO));
    }

    public void delete(Long id) {
        imageUrlRepository.deleteById(id);
    }

    public String findImage(Long id) {
        ImageUrlEntity imageUrlEntity = imageUrlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return imageUrlEntity.getImageId();
    }

    // 이미지에 대한 업로드 사용자ID 조회
    public Long getUploadedBy(String imageId) {
        return imageUrlRepository.findIdByImageId(imageId);
    }

    public ImageUrlEntity findByImageId(String imageId) {
        return imageUrlRepository.findByImageId(imageId);
    }

    public ImageUrlEntity findByImageId(String imageId) {
        return imageUrlRepository.findByImageId(imageId);
    }
}
