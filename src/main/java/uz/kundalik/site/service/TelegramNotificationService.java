package uz.kundalik.site.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.kundalik.telegram.enums.UserStatus;
import uz.kundalik.telegram.service.message.SendMsg;
import uz.kundalik.telegram.service.message.i18n;
import uz.kundalik.telegram.controller.KundalikBot;
import uz.kundalik.site.payload.TgNotificationUserBirthData;
import uz.kundalik.telegram.utils.Utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    private final i18n i18n;
    private final SendMsg sendMsg;
    private final KundalikBot kundalikBot;

    @Async
    @EventListener
    public void sendTgNotification(TgNotificationUserBirthData notificationUserBirthData) {
        UserStatus userStatus = notificationUserBirthData.getUserStatus();
        String langCode = notificationUserBirthData.getLangCode(); // hozir faqat "uz"
        Long chatId = notificationUserBirthData.getTelegramUserId();
        LocalDate birthDate = notificationUserBirthData.getBirthDate(); // ex: 2000-03-25
        String firstName = notificationUserBirthData.getFirstName() != null
                ? notificationUserBirthData.getFirstName()
                : "";

        LocalDate today = LocalDate.now();

        // 1) Topiladigan keyingi tug'ilgan kun (yil qismini yangilash)
        LocalDate nextBirthday = birthDate.withYear(today.getYear());
        if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today) && !nextBirthday.equals(today)) {
            // agar bu yilgi sana o'tgan bo'lsa yoki (saqlash uchun ehtiyot) keyingi yilga o'tkazamiz
            nextBirthday = nextBirthday.plusYears(1);
        }
        // Agar bugun bo'lsa, nextBirthday == today (special case handled below)
        if (nextBirthday.isBefore(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }

        long daysLeft = ChronoUnit.DAYS.between(today, nextBirthday);

        // 2) Agar tug'ilgan sana bugun bo'lsa (daysLeft == 0)
        int upcomingAge = nextBirthday.getYear() - birthDate.getYear();

        String sendText;
        if (daysLeft == 0) {
            // Bugun tug'ilgan kun
            sendText = String.format("<b>🎂 Tug‘ilgan kuningiz muborak, %s!</b>\n" +
                            "Bugun siz <b>%d yosh</b>ga to‘lasiz. Yaxshi kayfiyat va baxt tilaymiz! ✨",
                    escapeHtml(firstName), upcomingAge);
        } else if (userStatus == UserStatus.PREMIUM) {
            // Premium: chiroyli period (yil+oy+kun yoki oy+kun yoki kun)
            Period period = Period.between(today, nextBirthday);
            String formattedPeriod;
            if (period.getYears() > 0) {
                formattedPeriod = String.format("%d yil, %d oy va %d kun",
                        period.getYears(), period.getMonths(), period.getDays());
            } else if (period.getMonths() > 0) {
                formattedPeriod = String.format("%d oy va %d kun",
                        period.getMonths(), period.getDays());
            } else {
                formattedPeriod = String.format("%d kun", period.getDays());
            }

            sendText = String.format(
                    "<b>💎 Salom, %s!</b>\nTug‘ilgan kuningizga <b>%s</b> qoldi 🎉\n" +
                            "Siz <b>%d yosh</b>ga to‘lashga yaqinlashyapsiz. Yangi yilingizda cheksiz ilhom tilaymiz! 🌟",
                    escapeHtml(firstName), escapeHtml(formattedPeriod), upcomingAge
            );
        } else {
            // Oddiy user: kunlar bilan
            sendText = String.format(
                    "<b>🎉 Salom, %s!</b>\nTug‘ilgan kuningizga <b>%d kun</b> qoldi 🎂\n" +
                            "Yangi yoshingiz quvonch va omad olib kelsin! ✨",
                    escapeHtml(firstName), daysLeft
            );
        }

        // 3) Telegramga yuborish — HTML parse mode
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(sendText);
        message.setParseMode("HTML");
        message.disableWebPagePreview();

        kundalikBot.myExecute(message);
    }

    /**
     * Oddiy HTML escapefunction — ism yoki boshqa maydonlarda '<' '&' kabi belgilar bo'lsa xatolik bo'lmasligi uchun.
     * (Telegram HTML parse uchun minimal escaping)
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }


}
