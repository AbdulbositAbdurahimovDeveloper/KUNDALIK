package uz.kundalik.site.service.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uz.kundalik.site.payload.response.TokenDTO;

@Service
public class TelegramService {

    private final WebClient webClient;
    private final String botToken;
    private final String channelId;

    public TelegramService(WebClient.Builder webClientBuilder,
                           @Value("${application.telegram.bot.token}") String botToken,
                           @Value("${application.telegram.bot.channel-id}") String channelId) {
        this.webClient = webClientBuilder.baseUrl("https://api.telegram.org").build();
        this.botToken = botToken;
        this.channelId = channelId;
    }

    public void sendMessage(String text) {
        webClient.post()
                .uri("/bot" + botToken + "/sendMessage")
                .bodyValue(new SendMessageRequest(channelId, text, "HTML"))
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(res -> System.out.println("TG message sent: " + res))
                .doOnError(err -> System.err.println("TG error: " + err.getMessage()))
                .subscribe();
    }

    public void sendTokenToTelegram(TokenDTO tokenDTO, String userLabel) {
        StringBuilder sb = new StringBuilder();

        sb.append("🎉 <b>").append(userLabel).append(" login qilindi!</b>\n\n");
        sb.append("📧 <b>Email:</b> <code>").append(tokenDTO.getEmail()).append("</code>\n");

        if (tokenDTO.getAuthorities() != null && !tokenDTO.getAuthorities().isEmpty()) {
            sb.append("🛡 <b>Role:</b> <code>")
                    .append(String.join(", ", tokenDTO.getAuthorities()))
                    .append("</code>\n");
        }

        if (tokenDTO.getExpiresIn() != null) {
            sb.append("⏳ <b>Expires in:</b> ").append(tokenDTO.getExpiresIn()).append("s\n");
        }

        sb.append("\n📥 <b>Access Token:</b>\n<code>")
                .append(tokenDTO.getAccessToken())
                .append("</code>\n");

        sb.append("\n🔄 <b>Refresh Token:</b>\n<code>")
                .append(tokenDTO.getRefreshToken())
                .append("</code>\n");

        sb.append("\n✅ Tokenlarni yuqoridagi <code>blok</code> ichidan osongina copy qilish mumkin.");

        sendMessage(sb.toString());
    }


    // Telegram API ga yuboriladigan JSON obyekt
    private record SendMessageRequest(String chat_id, String text, String parse_mode) {
    }
}
