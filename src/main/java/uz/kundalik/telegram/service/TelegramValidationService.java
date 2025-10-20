package uz.kundalik.telegram.service;// TelegramValidationService.java

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TelegramValidationService {

    @Value("${application.telegram.bot.token}")
    private String botToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean validate(String initData) {
        try {
            String[] pairs = initData.split("&");
            Map<String, String> params = Arrays.stream(pairs)
                    .map(p -> p.split("=", 2))
                    .collect(Collectors.toMap(p -> decode(p[0]), p -> decode(p[1])));

            String receivedHash = params.remove("hash");
            if (receivedHash == null) {
                return false;
            }

            String dataCheckString = params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("\n"));

            SecretKeySpec secretKeySpec = new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            SecretKeySpec secretKey = new SecretKeySpec(mac.doFinal(botToken.getBytes(StandardCharsets.UTF_8)), "HmacSHA256");

            mac.init(secretKey);
            byte[] calculatedHashBytes = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));
            
            String calculatedHash = bytesToHex(calculatedHashBytes);
            
            return calculatedHash.equals(receivedHash);

        } catch (Exception e) {
            // Log the exception, e.g., using slf4j logger
            return false;
        }
    }

    @SneakyThrows
    public Long getUserId(String initData) {
        String[] pairs = initData.split("&");
        for (String pair : pairs) {
            if (pair.startsWith("user=")) {
                String userJson = decode(pair.substring(5));
                Map<String, Object> userMap = objectMapper.readValue(userJson, new TypeReference<>() {});
                return Long.parseLong(userMap.get("id").toString());
            }
        }
        throw new IllegalArgumentException("User ID not found in initData");
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
