package app.scit46.ufc.dto;

import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.PrivatePhotoEntity;
import app.scit46.ufc.entity.UserEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreatorDTO {

    private Long creatorId;
    private String intro;
    private PrivatePhotoEntity businessCert;
    private String bRegistNumber;
    private String bName;
    private String companyName;
    private String address;
    private PrivatePhotoEntity backImgUrl;
    private PrivatePhotoEntity proImgUrl;
    private UserEntity ownUser;
    private Boolean creatorStatus;

    public static CreatorDTO toDTO(CreatorEntity entity) {
        return CreatorDTO.builder()
                .creatorId(entity.getCreatorId())
                .intro(entity.getIntro())
                .businessCert(entity.getBusinessCert())
                .bRegistNumber(entity.getBRegistNumber())
                .bName(entity.getBName())
                .companyName(entity.getCompanyName())
                .address(entity.getAddress())
                .backImgUrl(entity.getBackImgUrl())
                .proImgUrl(entity.getProImgUrl())
                .ownUser(entity.getOwnUser())
                .creatorStatus(entity.getCreatorStatus())
                .build();
    }
}
