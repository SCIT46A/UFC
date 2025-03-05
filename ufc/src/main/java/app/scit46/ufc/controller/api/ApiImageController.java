package app.scit46.ufc.controller.api;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import app.scit46.ufc.service.ImageUrlService;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.cloudflare.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/image")
public class ApiImageController {

    // cloudflare 이미지 제어 서비스
    private final ImageService imageService;

    // 유저 서비스
    private final UserService userService;

    // 이미지 DB/URL 제어 서비스
    private final ImageUrlService imageUrlService;

    // 이미지 파일을 cloudflare에 업로드 후 이미지ID 반환
    // UserDetails 검토 필요
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // 파일이 비어있는지 확인
        log.warn("=== file : {}", file);
        if (file.isEmpty()) {
            log.error("=== file is empty");
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        String oauthId = request.getUserPrincipal().getName(); // OAuth 식별자
        Long userId = userService.findUserByIdentity(oauthId).getUserId();

        try {
            // 파일과 업로드한 유저의 ID를 파라미터로 전달하여 이미지 업로드/DB 저장 후 이미지ID 반환
            log.info("image upload start");
            String result = imageService.uploadImage(file, userId);   //Image Id
            //String imageUrl = ImageService.getImageUrl(result);
            log.info("image upload success");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("image upload error : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 이미지 ID를 통해 이미지 URL 반환
    @GetMapping("/{imageId}")

    public ResponseEntity<String> getImageUrl(@PathVariable("imageId") String imageId) {
        String result = imageService.getImageUrl(imageId);
        return ResponseEntity.ok(result);
    }

    // 이미지 URL 변환





    // 이미지 ID를 통해 이미지 삭제
    // UserDetails 검토 필요
    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<Boolean> deleteImage(@PathVariable String imageId, HttpServletRequest request) {
        String oauthId = request.getUserPrincipal().getName(); // OAuth 식별자
        Long userId = userService.findUserByIdentity(oauthId).getUserId();
        // 이미지 삭제 권한 검사
        if(userId != imageUrlService.getUploadedBy(imageId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }
        boolean result = imageService.deleteImage(imageId);
        return ResponseEntity.ok(result);
    }
}

