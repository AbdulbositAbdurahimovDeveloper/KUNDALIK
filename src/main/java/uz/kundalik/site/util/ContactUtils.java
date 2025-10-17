package uz.kundalik.site.util;

public class ContactUtils {

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private static final String PHONE_REGEX =
            "^\\+?[0-9]{9,15}$"; // xalqaro format, masalan: +998901234567

    public static boolean isEmail(String contact) {
        return contact != null && contact.matches(EMAIL_REGEX);
    }

    public static boolean isPhone(String contact) {
        return contact != null && contact.matches(PHONE_REGEX);
    }
}
