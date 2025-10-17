package uz.kundalik.site.config.logger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationStartupLogger {

    private final Environment env;

    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationStartup() {
        String appName = env.getProperty("spring.application.name", "Oromland");
        String serverPort = env.getProperty("server.port", "8080");
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Could not determine local host address", e);
        }

        log.info("\n" +
                        "-----------------------------------------------------------------------------------------\n" +
                        "✅  Application '{}' is running!\n" +
                        "   Access URLs:\n" +
                        "   - Local:      http://localhost:{}\n" +
                        "   - External:   http://{}:{}\n" +
                        "   Swagger UI:   http://localhost:{}/swagger-ui.html\n" +
                        "   Profile(s):   {}\n" +
                        "-----------------------------------------------------------------------------------------",
                appName,
                serverPort,
                hostAddress,
                serverPort,
                serverPort,
                env.getActiveProfiles().length == 0 ? "default" : String.join(", ", env.getActiveProfiles())
        );
    }
}