package uz.kundalik.telegram.payload.aladhan;

import lombok.Data;

@Data
@SuppressWarnings("SpellCheckingInspection")
public class PrayerData {
    private Timings timings;
    private DateInfo date;
    private Meta meta;
}
