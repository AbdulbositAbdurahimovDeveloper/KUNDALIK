package uz.kundalik.telegram.utils;

public interface Utils {

    String START = "/start";
    String UZBEKISTAN = "Uzbekistan";

    interface i18n {

        String ERROR_MESSAGE = "error_message";
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
        String PRAYER_TIME_HTML = "prayer_time_template_html";
        String PRAYER_NOT_FOUND  = "prayer_time_not_found";

        String CURRENCY_REPORT_HEADER_HTML = "currency.report.header.html";
        String CURRENCY_LINE_HTML = "currency.line.html";
        String CURRENCY_INFO_NOT_FOUND = "currency.info.not.found";
        String CURRENCY_FOOTER_HTML = "currency.footer.html";

        String CURRENCY_PRETTY_HEADER_HTML = "currency.pretty.header.html";
        String CURRENCY_PRETTY_LINE_HTML = "currency.pretty.line.html";
        String CURRENCY_PRETTY_FOOTER_HTML = "currency.pretty.footer.html";

        // Oddiy foydalanuvchi uchun
        String PRAYER_TIMES_FREE_FORMAT = "prayer_times_free_format";

        // Premium foydalanuvchi uchun
        String PRAYER_TIMES_PREMIUM_HEADER = "prayer_times_premium_header";
        String PRAYER_TIMES_PREMIUM_LINE_NEXT = "prayer_times_premium_line_next";
        String PRAYER_TIMES_PREMIUM_LINE_PAST = "prayer_times_premium_line_past";
        String PRAYER_TIMES_PREMIUM_LINE_FUTURE = "prayer_times_premium_line_future";
        String PRAYER_TIMES_PREMIUM_FOOTER = "prayer_times_premium_footer";
        String PRAYER_TIMES_PREMIUM_REMAINING_TIME_FORMAT = "prayer_times_premium_remaining_time_format";

        // Namoz nomlari
        String PRAYER_NAME_FAJR = "prayer_name_fajr";
        String PRAYER_NAME_DHUHR = "prayer_name_dhuhr";
        String PRAYER_NAME_ASR = "prayer_name_asr";
        String PRAYER_NAME_MAGHRIB = "prayer_name_maghrib";
        String PRAYER_NAME_ISHA = "prayer_name_isha";
        String FOUND_CITY_403 = "payer_city_forbidden";

        String BUTTON_WEATHER_HOURLY = "button_weather_hourly";
        String BUTTON_WEATHER_DAILY = "button_weather_daily";
        String BUTTON_WEATHER_REFRESH = "button_weather_refresh";
        String BUTTON_WEATHER_REMINDER = "button_weather_reminder";
        String BUTTON_WEATHER_OTHER_CITY = "button_weather_other_city";
        String ALERT_PREMIUM_FOR_REFRESH = "alert_premium_required_for_refresh";

        String CHOOSE_WEATHER_TIMING = "choose_weather_timing";

        String INFO_HOURLY_WEATHER = "info_hourly_weather";


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

        String ACTION_WEATHER = "whr";
        String ACTION_PRAYER = "P";
        String ACTION_CURRENCY = "C";
        String ACTION_NOTES = "N";
        String ACTION_REMINDER = "R";
        String ACTION_WALLET = "W";

        String ACTION_SEARCH = "search";
        String ACTION_CHOOSE = "choose";
        String ACTION_CITY = "city";
        String ACTION_LIST = "l";
        String ACTION_INFO = "info";
        String ACTION_REFRESH = "refresh";
        String ACTION_OTHER_CITY = "other_city";
        String ACTION_HOURLY = "hourly";
        String ACTION_DAILY = "daily";
        String ACTION_TIME = "time";

        String LOCK = "🔒 ";
        String ID = "id:";
        String ACTION_CANCEL = "cancel";


        String ACTION_PAGE = "page";
    }

    interface Command {
        String START = "/start";
        String STOP = "/stop";
        String HELP = "/help";
        String DASHBOARD = "/dashboard";
    }

    interface CurrencyFlags {

        // A
        String AED = "🇦🇪"; // BAA dirhami
        String AFN = "🇦🇫"; // Afg‘oniston afg‘onisi
        String AMD = "🇦🇲"; // Armaniston drami
        String ARS = "🇦🇷"; // Argentina pesosi
        String AUD = "🇦🇺"; // Avstraliya dollari
        String AZN = "🇦🇿"; // Ozarbayjon manati

        // B
        String BDT = "🇧🇩"; // Bangladesh takasi
        String BGN = "🇧🇬"; // Bolgariya levi
        String BHD = "🇧🇭"; // Bahrayn dinori
        String BND = "🇧🇳"; // Bruney dollari
        String BRL = "🇧🇷"; // Braziliya reali
        String BYN = "🇧🇾"; // Belorus rubli

        // C
        String CAD = "🇨🇦"; // Kanada dollari
        String CHF = "🇨🇭"; // Shveytsariya franki
        String CNY = "🇨🇳"; // Xitoy yuani
        String CUP = "🇨🇺"; // Kuba pesosi
        String CZK = "🇨🇿"; // Chexiya kronasi

        // D
        String DKK = "🇩🇰"; // Daniya kronasi
        String DZD = "🇩🇿"; // Jazoir dinori

        // E
        String EGP = "🇪🇬"; // Misr funti
        String EUR = "🇪🇺"; // EVRO

        // G
        String GBP = "🇬🇧"; // Angliya funt sterlingi
        String GEL = "🇬🇪"; // Gruziya larisi

        // H
        String HKD = "🇭🇰"; // Gongkong dollari
        String HUF = "🇭🇺"; // Vengriya forinti

        // I
        String IDR = "🇮🇩"; // Indoneziya rupiyasi
        String ILS = "🇮🇱"; // Isroil shekeli
        String INR = "🇮🇳"; // Hindiston rupiyasi
        String IQD = "🇮🇶"; // Iroq dinori
        String IRR = "🇮🇷"; // Eron riali
        String ISK = "🇮🇸"; // Islandiya kronasi

        // J
        String JOD = "🇯🇴"; // Iordaniya dinori
        String JPY = "🇯🇵"; // Yaponiya iyenasi

        // K
        String KGS = "🇰🇬"; // Qirg‘iz somi
        String KHR = "🇰🇭"; // Kambodja riyeli
        String KRW = "🇰🇷"; // Koreya Respublikasi voni
        String KWD = "🇰🇼"; // Quvayt dinori
        String KZT = "🇰🇿"; // Qozog‘iston tengesi

        // L
        String LAK = "🇱🇦"; // Laos kipisi
        String LBP = "🇱🇧"; // Livan funti
        String LYD = "🇱🇾"; // Liviya dinori

        // M
        String MAD = "🇲🇦"; // Marokash dirhami
        String MDL = "🇲🇩"; // Moldaviya leyi
        String MMK = "🇲🇲"; // Myanma kyati
        String MNT = "🇲🇳"; // Mongoliya tugriki
        String MXN = "🇲🇽"; // Meksika pesosi
        String MYR = "🇲🇾"; // Malayziya ringgiti

        // N
        String NOK = "🇳🇴"; // Norvegiya kronasi
        String NZD = "🇳🇿"; // Yangi Zelandiya dollari

        // O
        String OMR = "🇴🇲"; // Ummon riali

        // P
        String PHP = "🇵🇭"; // Filippin pesosi
        String PKR = "🇵🇰"; // Pokiston rupiyasi
        String PLN = "🇵🇱"; // Polsha zlotiysi

        // Q
        String QAR = "🇶🇦"; // Qatar riali

        // R
        String RON = "🇷🇴"; // Ruminiya leyi
        String RSD = "🇷🇸"; // Serbiya dinori
        String RUB = "🇷🇺"; // Rossiya rubli

        // S
        String SAR = "🇸🇦"; // Saudiya Arabistoni riali
        String SDG = "🇸🇩"; // Sudan funti
        String SEK = "🇸🇪"; // Shvetsiya kronasi
        String SGD = "🇸🇬"; // Singapur dollari
        String SYP = "🇸🇾"; // Suriya funti

        // T
        String THB = "🇹🇭"; // Tailand bati
        String TJS = "🇹🇯"; // Tojikiston somonisi
        String TMT = "🇹🇲"; // Turkmaniston manati
        String TND = "🇹🇳"; // Tunis dinori
        String TRY = "🇹🇷"; // Turkiya lirasi

        // U
        String UAH = "🇺🇦"; // Ukraina grivnasi
        String UYU = "🇺🇾"; // Urugvay pesosi
        String USD = "🇺🇸"; // AQSH dollari

        // V
        String VES = "🇻🇪"; // Venesuela bolivari
        String VND = "🇻🇳"; // Vetnam dongi

        // X
        String XDR = "🌍"; // SDR (Xalqaro Valyuta Fondi)

        // Y
        String YER = "🇾🇪"; // Yaman riali

        // Z
        String ZAR = "🇿🇦"; // Janubiy Afrika randi

        // Agar bayroq topilmasa ishlatiladigan standart belgi
        String DEFAULT_FLAG = "💰";
    }

    public interface ReplyButtons {
    }
}
