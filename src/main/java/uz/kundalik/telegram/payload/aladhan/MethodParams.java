package uz.kundalik.telegram.payload.aladhan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Metodning hisoblash burchaklari.
 */
@Data
@SuppressWarnings("SpellCheckingInspection")
public class MethodParams {
    @JsonProperty("Fajr")
    private double fajr;

    @JsonProperty("Isha")
    private double isha;
}