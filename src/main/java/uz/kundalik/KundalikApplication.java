package uz.kundalik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication
@ConfigurationPropertiesScan("uz.kundalik.site.properties")
public class KundalikApplication {

    public static void main(String[] args) {
        SpringApplication.run(KundalikApplication.class, args);
    }

}

