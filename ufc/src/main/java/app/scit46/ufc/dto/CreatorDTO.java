package app.scit46.ufc.dto;

import java.time.LocalDateTime;

import app.scit46.ufc.entity.CreatorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private LocalDate bRegistDate;
    private String bName;
    private String companyName;
    private String address;
    private ImageUrlDTO backImgUrl; // ✅ PhotoDTO 포함
    private ImageUrlDTO proImgUrl; // ✅ PhotoDTO 포함
    private UserDTO ownUser; // ✅ UserDTO 포함
    @JsonProperty
    private Boolean creatorStatus;

    public static CreatorDTO toDTO(CreatorEntity entity) {
        return CreatorDTO.builder()
                .creatorId(entity.getCreatorId())
                .intro(entity.getIntro())
                .businessCert(entity.getBusinessCert() != null ? ImageUrlDTO.toDTO(entity.getBusinessCert()) : null) // ✅
                                                                                                                     // //
                                                                                                                     // 변환
                .bRegistNumber(entity.getBRegistNumber())
                .bRegistDate(entity.getBRegistDate())
                .bName(entity.getBName())
                .companyName(entity.getCompanyName())
                .address(entity.getAddress())
                .bRegistDate(entity.getBRegistDate())
                .backImgUrl(entity.getBackImgUrl() != null ? ImageUrlDTO.toDTO(entity.getBackImgUrl()) : null) // ✅
                                                                                                               // PhotoDTO
                                                                                                               // 변환
                .proImgUrl(entity.getProImgUrl() != null ? ImageUrlDTO.toDTO(entity.getProImgUrl()) : null) // ✅
                                                                                                            // PhotoDTO
                                                                                                            // 변환
                .ownUser(entity.getOwnUser() != null ? UserDTO.toDTO(entity.getOwnUser()) : null) // ✅ UserDTO 변환
                .creatorStatus(entity.getCreatorStatus())
                .build();
    }
}
