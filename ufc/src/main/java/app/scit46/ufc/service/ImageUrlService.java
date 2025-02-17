package app.scit46.ufc.service;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.entity.ImageUrlEntity;
import app.scit46.ufc.repository.ImageUrlRepository;
import app.scit46.ufc.service.cloudflare.ImageService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageUrlService {

    private final ImageUrlRepository imageUrlRepository;

    public Long getId(String imageId){
        return imageUrlRepository.findIdByImageId(imageId);
    }

    public String getUrl(String imageId){
        return ImageService.getImageUrl(imageId);
    }

    // CRUD
    public void save(ImageUrlDTO imageUrlDTO){
        imageUrlRepository.save(ImageUrlEntity.toEntity(imageUrlDTO));
    }

    public void delete(Long id){
        imageUrlRepository.deleteById(id);
    }
}
