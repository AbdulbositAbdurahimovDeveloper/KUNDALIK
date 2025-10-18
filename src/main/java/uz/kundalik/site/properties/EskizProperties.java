package uz.kundalik.site.properties;

import lombok.Data;

@Data
public class EskizProperties {

    private Api api;
    private Sms sms;
    private boolean testMode;

    @Data
    public static class Api {
        private String url;
        private String email;
        private String token;
    }

    @Data
    public static class Sms {
        private String text;
    }
}
