package app.scit46.ufc.dto;

import java.time.LocalDateTime;

import app.scit46.ufc.entity.ImageUrlEntity;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUrlDTO {

        private Long id;
        private String imageId;
        private String filename;
        private LocalDateTime uploadedAt;
        private Long uploadedBy;

        public static ImageUrlDTO toDTO(ImageUrlEntity entity) {
                if (entity == null)
                        return null;

                return ImageUrlDTO.builder()
                                .imageId(entity.getImageId())
                                .filename(entity.getFilename())
                                .uploadedAt(entity.getUploadedAt())
                                .uploadedBy(entity.getUploadedBy().getUserId())
                                .build();
        }
}
