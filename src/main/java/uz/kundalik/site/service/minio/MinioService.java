package uz.kundalik.site.service.minio;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.kundalik.site.properties.MinioProperties;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public MinioService(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    public void uploadFile(String objectKey, MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBuckets().get(0))
                            .object(objectKey)
                            .stream(stream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            System.out.println("✅ Uploaded " + objectKey + " to bucket " + minioProperties.getBuckets().get(0));
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }


    /**
     * Fayl uchun pre-signed URL generatsiya qiladi.
     *
     * @param bucketName  MinIO bucket nomi
     * @param objectKey   Bucket ichidagi fayl nomi (path)
     * @return            Pre-signed URL (1 soat amal qiladi)
     */
    public String getPresignedUrl(String bucketName, String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(1, TimeUnit.HOURS)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating presigned URL", e);
        }
    }

    public void deleteFile(String bucketName, String objectKey) {
        // Haqiqiy MinIO'dan o'chirish logikasi
        // try {
        //     minioClient.removeObject(RemoveObjectArgs.builder()
        //         .bucket(bucketName)
        //         .object(objectKey)
        //         .build());
        // } catch (Exception e) {
        //     // Log error but don't rethrow, as DB record deletion is more critical
        //     log.error("Error deleting file {} from MinIO bucket {}", objectKey, bucketName, e);
        // }
        System.out.println("SIMULATING: Deleting " + objectKey + " from bucket " + bucketName);
    }
}