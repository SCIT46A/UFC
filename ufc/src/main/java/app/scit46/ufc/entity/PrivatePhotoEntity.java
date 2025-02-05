package app.scit46.ufc.entity;

import app.scit46.ufc.dto.PrivatePhotoDTO;
import app.scit46.ufc.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Privatephotos")
public class PrivatePhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoId;
    @Column(name = "origin_name")
    private Long originName;
    @Column(name = "convertName")
    private Long convertName;
    @CreationTimestamp
    @Column(name = "uploaded_date")
    private LocalDateTime uploadedDate;

    @OneToMany(mappedBy = "photoId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserEntity> users;


    public static PrivatePhotoEntity toEntity(PrivatePhotoDTO privatePhotoDTO) {
        return PrivatePhotoEntity.builder()
                .photoId(privatePhotoDTO.getPhotoId())
                .originName(privatePhotoDTO.getOriginName())
                .convertName(privatePhotoDTO.getConvertName())
                .uploadedDate(privatePhotoDTO.getUploadedDate())
                .build();
    }

}
