package app.scit46.ufc.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreatorCreateDTO {
    private String intro;
    // private String businessCert; // ✅ PhotoDTO 포함
    private String bRegistNumber;
    private String bName;
    private String companyName;
    private String address;
    private String backImgUrl; // ✅ PhotoDTO 포함
    private String proImgUrl;
}
