package uz.kundalik.telegram.service.message;

public interface i18n {

    /**
     * Berilgan key uchun mos tildagi matnni qaytaradi.
     * Agar topilmasa, default til yoki key o‘zi qaytariladi.
     */
    String get(String key, String langCode);

    /**
     * Faqat default til bo‘yicha matnni qaytaradi.
     */
    String getDefault(String key);
}
