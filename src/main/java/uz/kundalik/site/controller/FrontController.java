package uz.kundalik.site.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontController {

    /**
     * Forwards requests from the clean URL "/i18n" to the actual static file location.
     * The "forward:" prefix tells Spring's DispatcherServlet to handle the request
     * internally without sending a redirect to the client. The URL in the browser
     * remains "/i18n", providing a cleaner user experience.
     *
     * Spring Boot automatically serves static content from `/static`, so the path
     * `/i18n/i18n.html` is resolved relative to that directory.
     * @return The path to forward the request to.
     */
    @GetMapping("/i18n")
    public String i18nPage() {
        // Faylingiz: "resources/static/i18n/i18n.html"
        // Shuning uchun URL: "/i18n/i18n.html" bo'ladi
        return "forward:/i18n/i18n.html";
    }
}