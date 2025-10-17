package uz.kundalik.test;

import lombok.Data;
import java.time.DayOfWeek;

@Data
public class RecurrenceRequest {
    private RecurrenceFrequency frequency;
    private int hour;
    private int minute;
    private DayOfWeek dayOfWeek; // Haftalik eslatmalar uchun (MONDAY, TUESDAY, ...)
    private int dayOfMonth;      // Oylik eslatmalar uchun (1 dan 31 gacha)
}