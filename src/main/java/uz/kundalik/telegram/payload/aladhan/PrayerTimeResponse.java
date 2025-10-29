package uz.kundalik.telegram.payload.aladhan;

import lombok.Data;

@Data
@SuppressWarnings("SpellCheckingInspection")
public class PrayerTimeResponse {
    private int code;
    private String status;
    private PrayerData data;
}
