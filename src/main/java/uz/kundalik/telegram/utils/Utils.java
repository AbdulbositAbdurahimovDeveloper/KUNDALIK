package uz.kundalik.telegram.utils;

public interface Utils {

    String START = "/start";

    interface i18n {

        String LANGUAGE = "language";
        String CHOOSE_LANGUAGE = "choose_language";
        String MAIN_MENU = "main_menu";
        String SETTINGS = "settings";
        String CHANGE_LANGUAGE = "change_language";
        String BACK = "back";
        String HELP = "help";
        String CONTACT_ADMIN = "contact_admin";
        String ADMIN_CONTACT_TEXT = "admin_contact_text";
        String SHARE_CONTACT = "share_contact";
        String SEND_LOCATION = "send_location";
        String LOCATION = "location";
        String PHONE_NUMBER = "phone_number";
        String INVALID_PHONE_NUMBER = "invalid_phone_number";
        String WELCOME_TEXT = "welcome_text";
        String ALREADY_REGISTERED = "already_registered";
        String REGISTERED_SUCCESSFULLY = "registered_successfully";
        String NOT_REGISTERED = "not_registered";
        String ENTER_FULL_NAME = "enter_full_name";
        String ENTER_PHONE_NUMBER = "enter_phone_number";
        String ENTER_LOCATION = "enter_location";
        String REGISTRATION_CANCELLED = "registration_cancelled";
        String REGISTRATION_TIMEOUT = "registration_timeout";
        String UNKNOWN_COMMAND = "unknown_command";
        String ERROR = "error";
        String CANCEL = "cancel";
        String CONFIRM = "confirm";

    }

    interface InlineButtons {

        String PAGINATION_PREVIOUS_TEXT = "⬅️ Oldingi";
        String PAGINATION_NEXT_TEXT = "Keyingi ➡️";

    }

    interface Action {

        String ACTION_LIST = "l";


        String ACTION_PAGE = "page";
    }
}
