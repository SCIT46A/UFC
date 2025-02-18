package app.scit46.ufc.dto;

import app.scit46.ufc.entity.CreatorEntity;
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
    private Long businessCert;
    private String bRegistNumber;
    private String bName;
    private String companyName;
    private String address;
    private Long backImgUrl;
    private Long proImgUrl;
    private Long ownUser;

    public static CreatorDTO toDTO(CreatorEntity entity) {
        return CreatorDTO.builder()
                .creatorId(entity.getCreatorId())
                .intro(entity.getIntro())
                .businessCert(entity.getBusinessCert().getId())
                .bRegistNumber(entity.getBRegistNumber())
                .bName(entity.getBName())
                .companyName(entity.getCompanyName())
                .address(entity.getAddress())
                .backImgUrl(entity.getBackImgUrl().getId())
                .proImgUrl(entity.getProImgUrl().getId())
                .ownUser(entity.getOwnUser().getUserId())
                .build();
    }
}
