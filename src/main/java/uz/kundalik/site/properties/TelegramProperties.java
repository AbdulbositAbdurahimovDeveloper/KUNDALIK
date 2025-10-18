package uz.kundalik.site.properties;

import lombok.Data;

@Data
public class TelegramProperties {

    private Bot bot;

    @Data
    public static class Bot {
        private String token;
        private String username;
        private String webhookPath;
        private String chatId;
        private String channelId;
    }
}
