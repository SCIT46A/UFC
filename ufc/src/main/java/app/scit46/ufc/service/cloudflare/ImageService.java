package app.scit46.ufc.service.cloudflare;

import java.util.UUID;

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

/**
 * 이곳의 서비스는 CloudFlare API와 직접 통신하는 내용만 기술하였습니다.
 * 이미지 업로드 및 기타 DB(CRUD)가 필요한 비즈니스 로직은 ImageUrlService 에서 처리합니다.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    @Value("${cloudflare.account-id}")
    private String accountId;

    @Value("${cloudflare.api-token}")
    private String apiToken;

    @Value("${cloudflare.account-hash}")
    private String accountHash;

    // ------------------------------------------------------------------------------------------------

    private final RestTemplate restTemplate = new RestTemplate();

    private final ImageUrlService imageUrlService;    //사진Url DB 수정시 사용

    // 이미지 업로드
    public String uploadImage(MultipartFile file, Long userId) {
        // 유효성 검사
        // 파일이 없거나 사용자 ID가 없는 경우 null 반환
        //if(file == null || userId == null) return null;
        
        // Cloudflare IMAGE Upload API URL
        String url = "https://api.cloudflare.com/client/v4/accounts/" + accountId + "/images/v1";
        // Cloudflare API 호출 형식 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        // 인증 토큰 설정
        headers.set("Authorization", "Bearer " + apiToken);
        // 콘텐츠 형식 설정
        headers.set("Content-Type", "multipart/form-data");

        // 이미지 파일 업로드 형식 바디 설정
        // LinkedMultiValueMap : 여러 값을 가질 수 있는 맵, 파일 업로드(multipart/form-data) 형식 지원
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        // 요청할 내용에 파일 첨부(구성)
        body.add("file", file);

        // 요청 엔티티 생성(헤더, 바디 설정)
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        // 요청 결과 변수 선언
        ResponseEntity<ApiResponse> response = null;
        try{
            // 요청 결과 반환
            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResponse.class);
        }catch(Exception e){
            // 오류 발생시 오류 로그 출력
            log.error("imgsrv.UPL - Cloudflare API 호출 오류", e);
            // 오류 출력
            e.printStackTrace();
            // 오류 발생시 null 반환
            return null;
        }
        // TODO: 이미지ID, 원본이름 db에 저장로직 추가
        // 이미지 업로드 성공 후 반환된 이미지ID, 원본이름, 업로드 사용자 정보 저장
        imageUrlService.save(
            ImageUrlDTO.builder()
            .imageId(response.getBody().getResult().getId())
            .filename(response.getBody().getResult().getFilename())
            .uploadedBy(userId)
            .build());
        return response.getBody().getResult().getId(); // ImageId
    }

    // 이미지 삭제
    public boolean deleteImage(String imageId) {
        // 유효성 검사
        // imageId가 null 경우 false 반환
        if(imageId == null) return false;
        // imageID가 UUID 형식인지 검사
        if (!UUID.fromString(imageId).toString().equals(imageId)){
            log.error("imgsrv.DEL - 이미지 ID가 유효하지 않습니다.");
            return false;
        }
        // Cloudflare IMAGE Delete API URL
        String url = "https://api.cloudflare.com/client/v4/accounts/" + accountId + "/images/v1/" + imageId;
        // Cloudflare API 호출 형식 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        // 인증 토큰 설정
        headers.set("Authorization", "Bearer " + apiToken);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);

        try{
            restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, ApiResponse.class);
        }catch(Exception e){
            log.error("Cloudflare API 호출 오류", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 이미지 URL 변환
    public String getImageUrl(String imageId) {
        if(imageId == null) return null;
        if(!UUID.fromString(imageId).toString().equals(imageId)){
            log.error("imgsrv.GET - 이미지 ID가 유효하지 않습니다.");
            return null;
        }
        return "https://imagedelivery.net/" + accountHash + "/" + imageId + "/public";
    }

}
