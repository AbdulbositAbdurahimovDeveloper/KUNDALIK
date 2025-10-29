package uz.kundalik.telegram.payload.aladhan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * So'rov haqidagi meta-ma'lumotlar.
 */
@Data
@SuppressWarnings("SpellCheckingInspection")
public class Meta {
    private double latitude;
    private double longitude;
    private String timezone;
    private MethodInfo method;

    @JsonProperty("latitudeAdjustmentMethod")
    private String latitudeAdjustmentMethod;

    @JsonProperty("midnightMode")
    private String midnightMode;

    private String school;
    private Offset offset;
}