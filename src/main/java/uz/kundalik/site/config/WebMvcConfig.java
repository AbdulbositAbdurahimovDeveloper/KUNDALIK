package uz.kundalik.site.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uz.kundalik.site.config.logger.LoggingInterceptor;
import uz.kundalik.site.properties.ApplicationProperties;
import uz.kundalik.site.properties.MinioProperties;

import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan("uz.kundalik")
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;
    private final ApplicationProperties applicationProperties;

    MinioProperties minio() {
        return applicationProperties.getMinio();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loggingInterceptor)
                .excludePathPatterns("/telegram-bot");

    }

    @Bean
    public ValidatorFactory validatorFactory() {
        return Validation.buildDefaultValidatorFactory();
    }

    @Bean
    public Validator validator() {
        return validatorFactory().getValidator();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient client = MinioClient.builder()
                .endpoint(minio().getEndpoint())
                .credentials(minio().getAccessKey(), minio().getSecretKey())
                .build();

        for (String bucket : minio().getBuckets()) {

            BucketExistsArgs imaExistsArgs = BucketExistsArgs.builder().bucket(bucket).build();
            if (!client.bucketExists(imaExistsArgs)) {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build());
            }
        }
        return client;
    }


//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedMethods("*")
//                .allowedHeaders("*")
//                .allowCredentials(false);
//    }
//
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // ngrok bergan manzil yoki Telegramdan keladigan manbalarga ruxsat berish
//        configuration.setAllowedOrigins(List.of("https://*.ngrok-free.app", "https://web.telegram.org"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        // ENG MUHIM QISM: Nostandart sarlavhaga ruxsat berish
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Telegram-Init-Data"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // BARCHA MANBALARGA RUXSAT BERISH (test uchun eng oson yo'l)
        configuration.setAllowedOrigins(List.of("*"));

        // Barcha metodlarga ruxsat berish
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ENG MUHIM QISM: BARCHA SARLAVHALARGA RUXSAT BERISH
        configuration.setAllowedHeaders(List.of("*"));

        // Credentials'ga ruxsat (kerak bo'lmasligi mumkin, lekin zarari yo'q)
        // configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Barcha yo'llar uchun shu konfiguratsiyani qo'llash
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
