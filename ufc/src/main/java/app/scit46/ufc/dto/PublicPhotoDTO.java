package app.scit46.ufc.dto;

import app.scit46.ufc.entity.PublicPhotoEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PublicPhotoDTO {

    private Long photoId;
    private String originName;
    private String convertName;
    private LocalDateTime uploadedDate;

    public static PublicPhotoDTO toDTO(PublicPhotoEntity entity) {
        return PublicPhotoDTO.builder()
                .photoId(entity.getPhotoId())
                .originName(entity.getOriginName())
                .convertName(entity.getConvertName())
                .uploadedDate(entity.getUploadedDate())
                .build();
    }
}
