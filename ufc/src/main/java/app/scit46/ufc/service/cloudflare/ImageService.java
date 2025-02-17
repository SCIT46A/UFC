package app.scit46.ufc.service.cloudflare;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.cloudflare.ApiResponse;
import app.scit46.ufc.service.ImageUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    @Value("${cloudflare.account-id}")
    private String accountId;

    @Value("${cloudflare.api-token}")
    private String apiToken;

    @Value("${cloudflare.account-hash}")
    private static String accountHash;

    // ------------------------------------------------------------------------------------------------

    private final RestTemplate restTemplate = new RestTemplate();

    private final ImageUrlService imageUrlService;    //사진Url DB 수정시 사용

    public String uploadImage(MultipartFile file) {
        String url = "https://api.cloudflare.com/client/v4/accounts/" + accountId + "/images/v2";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.set("Content-Type", "multipart/form-data");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<ApiResponse> response = null;
        try{
            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResponse.class);
        }catch(Exception e){
            log.error("Cloudflare API 호출 오류", e);
            e.printStackTrace();
            return null;
        }
        // TODO: 이미지ID, 원본이름 db에 저장로직 추가
        imageUrlService.save(
            ImageUrlDTO.builder()
            .imageId(response.getBody().getResult().getId())
            .filename(response.getBody().getResult().getFilename())
            .build());
        return response.getBody().getResult().getId(); // ImageId
    }

    public static String getImageUrl(String imageId) {
        return "https://imagedelivery.net/"+accountHash+"/"+imageId+"/public";
    }

    public void deleteImage(String imageId) {
        String url = "https://api.cloudflare.com/client/v4/accounts/" + accountId + "/images/v1/" + imageId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);

        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, ApiResponse.class);
    }
}
