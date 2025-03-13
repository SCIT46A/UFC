package app.scit46.ufc.dto.cloudflare;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Image {
    private String id; // Image unique identifier
    private String filename; // Image file name
    private Object meta; // User modifiable key-value store
    private boolean requireSignedURLs; // Indicates if signed URLs are required
    private LocalDateTime uploaded; // When the media item was uploaded
    private List<String> variants; // Available variants for an image
}