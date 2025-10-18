package uz.kundalik.site.properties;

import lombok.Data;
import java.util.List;

@Data
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private List<String> buckets;
}
