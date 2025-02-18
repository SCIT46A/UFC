package app.scit46.ufc.dto;

import java.time.LocalDateTime;

import app.scit46.ufc.entity.ImageUrlEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUrlDTO {
    private Long id;
    private String imageId;
    private String filename;
    private LocalDateTime uploadedAt;
    private Long uploadedBy;

    public static ImageUrlDTO toDTO(ImageUrlEntity entity){
        return ImageUrlDTO.builder()
                .id(entity.getId())
                .imageId(entity.getImageId())
                .filename(entity.getFilename())
                .uploadedAt(entity.getUploadedAt())
                .uploadedBy(entity.getUploadedBy().getUserId())
                .build();
    }
}
