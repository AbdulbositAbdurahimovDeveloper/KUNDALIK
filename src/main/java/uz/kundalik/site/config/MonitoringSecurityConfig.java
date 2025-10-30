package uz.kundalik.site.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class MonitoringSecurityConfig {

    @Bean
    @Order(1) // Bu security chain'ni birinchi bo'lib tekshirilishini ta'minlaydi
    public SecurityFilterChain monitoringSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/actuator/**") // Faqat /actuator/** yo'llari uchun ishlaydi
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll() // /actuator/** ostidagi barcha so'rovlarga ruxsat beramiz
            )
            .csrf(AbstractHttpConfigurer::disable); // Actuator uchun CSRF himoyasini o'chiramiz

        return http.build();
    }
}