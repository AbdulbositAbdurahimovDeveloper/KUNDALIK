package uz.kundalik.test;

import java.time.DayOfWeek;

// Bu metod biror Service yoki Helper klassida joylashgan bo'ladi
public class ReminderHelperService {

    /**
     * Foydalanuvchi tanlovlari asosida Cron expression'ni qo'lda yasaydi.
     * Cron formati: (daqiqa) (soat) (oy_kuni) (oy) (hafta_kuni)
     */
    public String generateCronExpressionManually(RecurrenceRequest request) {
        if (request.getHour() < 0 || request.getHour() > 23 || request.getMinute() < 0 || request.getMinute() > 59) {
            throw new IllegalArgumentException("Noto'g'ri soat yoki daqiqa kiritildi.");
        }

        String minute = String.valueOf(request.getMinute());
        String hour = String.valueOf(request.getHour());
        String dayOfMonth = "*";
        String month = "*";
        String dayOfWeek = "*";

        switch (request.getFrequency()) {
            case DAILY:
                // Hech narsa o'zgarmaydi, hamma yulduzcha (*) qoladi
                break;
            case WEEKLY:
                if (request.getDayOfWeek() == null) {
                    throw new IllegalArgumentException("Hafta kuni ko'rsatilmagan.");
                }
                // DayOfWeek.getValue() 1 (Dushanba) dan 7 (Yakshanba) gacha qiymat qaytaradi
                dayOfWeek = String.valueOf(request.getDayOfWeek().getValue());
                break;
            case MONTHLY:
                if (request.getDayOfMonth() < 1 || request.getDayOfMonth() > 31) {
                    throw new IllegalArgumentException("Oy kuni noto'g'ri kiritilgan.");
                }
                dayOfMonth = String.valueOf(request.getDayOfMonth());
                break;
        }

        return String.format("%s %s %s %s %s", minute, hour, dayOfMonth, month, dayOfWeek);
    }
}