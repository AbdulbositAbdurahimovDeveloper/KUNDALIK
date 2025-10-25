package uz.kundalik.telegram.payload.aladhan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Har bir namoz vaqti uchun daqiqalardagi tuzatishlar (offset).
 */
@Data
@SuppressWarnings("SpellCheckingInspection")
public class Offset {
    @JsonProperty("Imsak")
    private int imsak;

    @JsonProperty("Fajr")
    private int fajr;

    @JsonProperty("Sunrise")
    private int sunrise;

    @JsonProperty("Dhuhr")
    private int dhuhr;

    @JsonProperty("Asr")
    private int asr;

    @JsonProperty("Maghrib")
    private int maghrib;

    @JsonProperty("Sunset")
    private int sunset;

    @JsonProperty("Isha")
    private int isha;

    @JsonProperty("Midnight")
    private int midnight;
}