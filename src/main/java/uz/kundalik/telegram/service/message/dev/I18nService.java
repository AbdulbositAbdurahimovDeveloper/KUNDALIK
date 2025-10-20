package uz.kundalik.telegram.service.message.dev;

public interface I18nService {
    /**
     * Berilgan kalit va til kodi bo'yicha tarjimani oladi.
     * @param key Tarjima kaliti (masalan, "welcome_message")
     * @param langCode Til kodi (masalan, "uz", "en")
     * @return Topilgan tarjima yoki tarjima topilmasa kalitning o'zi.
     */
    String get(String key, String langCode);

    /**
     * Tarjimani oladi va uni berilgan argumentlar bilan formatlaydi.
     * Misol: "Salom, %s!" va "Ali" argumenti => "Salom, Ali!"
     * @param key Tarjima kaliti
     * @param langCode Til kodi
     * @param args Formatlash uchun argumentlar
     * @return Formatlangan tarjima.
     */
    String get(String key, String langCode, Object... args);
}