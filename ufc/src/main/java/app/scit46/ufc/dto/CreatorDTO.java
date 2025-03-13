package app.scit46.ufc.dto;

import app.scit46.ufc.entity.CreatorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreatorDTO {
    private Long creatorId;
    private String intro;
    private ImageUrlDTO businessCert; // ✅ PhotoDTO 포함
    private String bRegistNumber;
    private String bName;
    private String companyName;
    private String address;
    private LocalDateTime bRegistDate;
    private ImageUrlDTO backImgUrl; // ✅ PhotoDTO 포함
    private ImageUrlDTO proImgUrl; // ✅ PhotoDTO 포함
    private UserDTO ownUser; // ✅ UserDTO 포함
    private Boolean creatorStatus;

//  메시지 부분 사용하기 위해 제작
// 새 생성자: bName와 proImgUrl 값만 필요할 경우 (proImgUrl은 imageId로 사용)
public CreatorDTO(Long creatorId, String bName, String proImgUrl) {
    this.creatorId = creatorId;
    this.bName = bName;
    // proImgUrl 문자열을 ImageUrlDTO의 imageId에 대입하여 생성
    this.proImgUrl = ImageUrlDTO.builder()
            .imageId(proImgUrl)
            .build();
}

    public static CreatorDTO toDTO(CreatorEntity entity) {
        return CreatorDTO.builder()
                .creatorId(entity.getCreatorId())
                .intro(entity.getIntro())
                .businessCert(entity.getBusinessCert() != null ? ImageUrlDTO.toDTO(entity.getBusinessCert()) : null) // ✅                                                                                       // 변환
                .bRegistNumber(entity.getBRegistNumber())
                .bName(entity.getBName())
                .companyName(entity.getCompanyName())
                .address(entity.getAddress())
                .bRegistDate(entity.getBRegistDate())
                .backImgUrl(entity.getBackImgUrl() != null ? ImageUrlDTO.toDTO(entity.getBackImgUrl()) : null) // ✅ PhotoDTO 변환
                .proImgUrl(entity.getProImgUrl() != null ? ImageUrlDTO.toDTO(entity.getProImgUrl()) : null) // ✅ PhotoDTO 변환
                .ownUser(entity.getOwnUser() != null ? UserDTO.toDTO(entity.getOwnUser()) : null) // ✅ UserDTO 변환
                .creatorStatus(entity.getCreatorStatus())
                .build();
    }
}
