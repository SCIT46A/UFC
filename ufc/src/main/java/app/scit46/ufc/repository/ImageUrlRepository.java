package app.scit46.ufc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.ImageUrlEntity;

public interface ImageUrlRepository extends JpaRepository<ImageUrlEntity, Long> {
    Long findIdByImageId(String imageId);

    ImageUrlEntity findByImageId(String imageId);
}
