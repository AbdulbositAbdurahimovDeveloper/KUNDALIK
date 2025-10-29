package uz.kundalik.telegram.payload.aladhan;

import lombok.Data;

import java.util.List;

/**
 * Hijriy sana ma'lumotlari.
 */
@Data
@SuppressWarnings("SpellCheckingInspection")
public class HijriDate {
    private String date;
    private String format;
    private String day;
    private Weekday weekday;
    private Month month;
    private String year;
    private Designation designation;
    private List<String> holidays;
}