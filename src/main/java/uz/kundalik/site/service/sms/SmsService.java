package uz.kundalik.site.service.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SmsService {

    private final WebClient webClient;
    private final String eskizApiUrl;
    private final String eskizToken;
    private final TelegramService telegramService;
    private final boolean testMode;

    public SmsService(WebClient.Builder webClientBuilder,
                      @Value("${eskiz.api.url}") String eskizApiUrl,
                      @Value("${eskiz.api.token}") String eskizToken,
                      @Value("${eskiz.test-mode:true}") boolean testMode,
                      TelegramService telegramService) {
        this.webClient = webClientBuilder.build();
        this.eskizApiUrl = eskizApiUrl;
        this.eskizToken = eskizToken;
        this.testMode = testMode;
        this.telegramService = telegramService;
    }

    public Mono<String> sendSms(String phoneNumber, String code) {
        if (testMode) {
            // 🔹 Test rejimida → faqat Telegramga
            String smsMessage = "This is test from Eskiz";

            String tgMessage = String.format(
                    "🧪 TEST SMS LOG\n\n" +
                            "👤 Phone: %s\n" +
                            "🔑 Code: %s\n" +
                            "📩 Fake text: %s\n\n" +
                            "--- Simulation ---\n" +
                            "User would actually receive:\n" +
                            "\"Sizning tasdiqlash kodingiz: %s\"",
                    phoneNumber, code, "This is test from Eskiz", code
            );

            telegramService.sendMessage(tgMessage);
            return Mono.just("Test mode: SMS yuborilmadi, faqat Telegramga log tashlandi");
        }


        // ✅ Prod rejimida → SMS yuboriladi
        String smsMessage = "Sizning tasdiqlash kodingiz: " + code;

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("mobile_phone", phoneNumber.replaceAll("\\+", ""));
        formData.add("message", smsMessage);
        formData.add("from", "4546");

        return webClient.post()
                .uri(eskizApiUrl)
                .header("Authorization", "Bearer " + eskizToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("✅ SMS sent successfully: " + response))
                .doOnError(error -> System.err.println("❌ Error sending SMS: " + error.getMessage()));
    }
}
