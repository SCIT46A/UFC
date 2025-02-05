package app.scit46.ufc.dto;

import app.scit46.ufc.entity.PrivatePhotoEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PrivatePhotoDTO {

    private Long photoId;
    private String originName;
    private String convertName;
    private LocalDateTime uploadedDate;

    public static PrivatePhotoDTO toDTO(PrivatePhotoEntity entity) {
        return PrivatePhotoDTO.builder()
                .photoId(entity.getPhotoId())
                .originName(entity.getOriginName())
                .convertName(entity.getConvertName())
                .uploadedDate(entity.getUploadedDate())
                .build();
    }
}
