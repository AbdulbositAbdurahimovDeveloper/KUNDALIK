package uz.kundalik.telegram.utils;

public interface Utils {

    String START = "/start";

    interface i18n {

        String MESSAGE_AUTH_REQUIRED = "message.auth_required";
        String MESSAGE_PREMIUM_REQUIRED = "message.premium_required";
        String MESSAGE_IN_DEVELOPMENT = "message.in_development";


        String WELCOME_MESSAGE = "welcome_message";
        String BUTTON_WEATHER = "button.weather";
        String BUTTON_PRAYER = "button.prayer";
        String BUTTON_CURRENCY = "button.currency";
        String BUTTON_NOTES = "button.notes";
        String BUTTON_REMINDER = "button.reminder";
        String BUTTON_BIRTHDATE = "button.birthdate";
        String BUTTON_WALLET = "button.wallet";
        String BUTTON_SETTINGS = "button.settings";
        String BUTTON_SEND_LOCATION = "button_send_location";
        String BUTTON_MAIN_MENU = "button_main_menu";
        String WEATHER_INFO = "weather_info";
        String CHOOSE_LOCATION = "choose_location";
        String NOT_FOUND_CITY = "not_found_city";
        String FOUND_CITY = "found_city";
        String ONE_CITY = "one_city";
        String ERROR_MESSAGE = "error_message";


//        String LANGUAGE = "language";
//        String CHOOSE_LANGUAGE = "choose_language";
//        String MAIN_MENU = "main_menu";
//        String SETTINGS = "settings";
//        String CHANGE_LANGUAGE = "change_language";
//        String BACK = "back";
//        String HELP = "help";
//        String CONTACT_ADMIN = "contact_admin";
//        String ADMIN_CONTACT_TEXT = "admin_contact_text";
//        String SHARE_CONTACT = "share_contact";
//        String SEND_LOCATION = "send_location";
//        String LOCATION = "location";
//        String PHONE_NUMBER = "phone_number";
//        String INVALID_PHONE_NUMBER = "invalid_phone_number";
//        String WELCOME_TEXT = "welcome_text";
//        String ALREADY_REGISTERED = "already_registered";
//        String REGISTERED_SUCCESSFULLY = "registered_successfully";
//        String NOT_REGISTERED = "not_registered";
//        String ENTER_FULL_NAME = "enter_full_name";
//        String ENTER_PHONE_NUMBER = "enter_phone_number";
//        String ENTER_LOCATION = "enter_location";
//        String REGISTRATION_CANCELLED = "registration_cancelled";
//        String REGISTRATION_TIMEOUT = "registration_timeout";
//        String UNKNOWN_COMMAND = "unknown_command";
//        String ERROR = "error";
//        String CANCEL = "cancel";
//        String CONFIRM = "confirm";

    }

    interface InlineButtons {

        String PAGINATION_PREVIOUS_TEXT = "⬅️ Oldingi";
        String PAGINATION_NEXT_TEXT = "Keyingi ➡️";

    }

    interface Action {

        String ACTION_WEATHER = "W";
        String ACTION_PRAYER = "P";
        String ACTION_CURRENCY = "C";
        String ACTION_NOTES = "N";
        String ACTION_REMINDER = "R";
        String ACTION_WALLET = "W";

        String ACTION_SEARCH = "search";
        String ACTION_CHOOSE = "choose";
        String ACTION_CITY = "city";
        String ACTION_LIST = "l";

        String LOCK = "🔒 ";
        String ID = "id:";


        String ACTION_PAGE = "page";
    }

    interface Command {
        String START = "/start";
        String STOP = "/stop";
        String HELP = "/help";
        String DASHBOARD = "/dashboard";
    }

    public interface ReplyButtons {
    }
}
