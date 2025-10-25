package uz.kundalik.telegram.payload.aladhan;

import lombok.Data;

/**
 * Sana haqidagi ma'lumotlarni (milodiy va hijriy) saqlaydi.
 */
@Data
@SuppressWarnings("SpellCheckingInspection")
public class DateInfo {
    private String readable;
    private String timestamp;
    private HijriDate hijri;
    private GregorianDate gregorian;
}