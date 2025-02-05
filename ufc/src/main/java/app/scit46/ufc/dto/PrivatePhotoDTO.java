package app.scit46.ufc.dto;

import app.scit46.ufc.entity.PrivatePhotoEntity;
import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PrivatePhotoDTO {
    private Long photoId;
    private Long originName;
    private Long convertName;
    private LocalDateTime uploadedDate;

    public static PrivatePhotoDTO toDTO(PrivatePhotoEntity privatePhotoEntity) {
        return PrivatePhotoDTO.builder()
                .photoId(privatePhotoEntity.getPhotoId())
                .originName(privatePhotoEntity.getOriginName())
                .convertName(privatePhotoEntity.getConvertName())
                .uploadedDate(privatePhotoEntity.getUploadedDate())
                .build();
    }
}
