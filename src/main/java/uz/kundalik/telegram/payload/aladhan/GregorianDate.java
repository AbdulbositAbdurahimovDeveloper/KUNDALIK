package uz.kundalik.telegram.payload.aladhan;

import lombok.Data;

/**
 * Milodiy sana ma'lumotlari.
 */
@Data
@SuppressWarnings("SpellCheckingInspection")
public class GregorianDate {
    private String date;
    private String format;
    private String day;
    private Weekday weekday;
    private Month month;
    private String year;
    private Designation designation;
}