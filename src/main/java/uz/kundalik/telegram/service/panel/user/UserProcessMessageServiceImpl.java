package uz.kundalik.telegram.service.panel.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.kundalik.site.model.Address;
import uz.kundalik.site.model.User;
import uz.kundalik.telegram.controller.KundalikBot;
import uz.kundalik.telegram.enums.UserState;
import uz.kundalik.telegram.enums.UserStatus;
import uz.kundalik.telegram.model.TelegramUser;
import uz.kundalik.telegram.payload.currency.CurrencyRateDTO;
import uz.kundalik.telegram.payload.prayer.PrayerDayDTO;
import uz.kundalik.telegram.payload.weather.WeatherResponseDTO;
import uz.kundalik.telegram.payload.weather.search.SearchLocationDTO;
import uz.kundalik.telegram.repository.LanguageRepository;
import uz.kundalik.telegram.repository.TelegramUserRepository;
import uz.kundalik.telegram.service.RoleService;
import uz.kundalik.telegram.service.TelegramHelperService;
import uz.kundalik.telegram.service.UserStateService;
import uz.kundalik.telegram.service.api.CurrencyApi;
import uz.kundalik.telegram.service.api.IslomApi;
import uz.kundalik.telegram.service.api.WeatherApi;
import uz.kundalik.telegram.service.keybard.InlineKeyboardService;
import uz.kundalik.telegram.service.keybard.user.UserInlineKeyboardService;
import uz.kundalik.telegram.service.keybard.user.UserReplyKeyboardService;
import uz.kundalik.telegram.service.message.GenerationMessageService;
import uz.kundalik.telegram.service.message.SendMsg;
import uz.kundalik.telegram.service.message.i18n;
import uz.kundalik.telegram.utils.Utils;

import java.util.List;

import static uz.kundalik.telegram.utils.Utils.*;

@Service
@RequiredArgsConstructor
public class UserProcessMessageServiceImpl implements UserProcessMessageService {

    private final i18n i18n;
    private final SendMsg sendMsg;
    private final RoleService roleService;
    private final KundalikBot kundalikBot;
    private final UserStateService userStateService;
    private final LanguageRepository languageRepository;
    private final TelegramHelperService telegramHelperService;
    private final TelegramUserRepository telegramUserRepository;
    private final UserReplyKeyboardService userReplyKeyboardService;
    private final WeatherApi weatherApi;
    private final UserInlineKeyboardService userInlineKeyboardService;
    private final IslomApi islomApi;
    private final GenerationMessageService generationMessageService;
    private final CurrencyApi currencyApi;
    private final InlineKeyboardService inlineKeyboardService;

    @Override
    public void processMessage(Message message) {

        Long chatId = message.getChatId();

        String langCode = telegramHelperService.langCode(chatId);
        UserStatus userStatus = telegramHelperService.userStatus(chatId);
        UserState userState = telegramHelperService.userState(chatId);

        if (message.hasText()) {

            String text = message.getText();

            if (text.equals("/login")) {

                InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.welcomeFirstTime(chatId);
                SendMessage sendMessage = sendMsg.sendMessage(chatId, "login", inlineKeyboardMarkup);
                kundalikBot.myExecute(sendMessage);

            }

            if (text.equals(Command.START)) {

                commandStart(message, langCode, chatId, userStatus);

            } else if (text.equals(Command.HELP)) {

                commandHelp(chatId, langCode);

            } else if (text.equals(Command.DASHBOARD)) {

                commandDashboard(chatId, langCode);

            } else if (text.equals(i18n.get(Utils.i18n.BUTTON_MAIN_MENU, langCode))) {

                buttonMainMenu(chatId, langCode, userStatus);

            } else if (text.equals(i18n.get(Utils.i18n.BUTTON_WEATHER, langCode))) {

                buttonWeather(chatId, langCode, userStatus);

            } else if (text.equals(i18n.get(Utils.i18n.BUTTON_PRAYER, langCode))) {

                buttonPrayer(chatId, langCode, userStatus);

            } else if (text.equals(i18n.get(Utils.i18n.BUTTON_CURRENCY, langCode))) {

                buttonCurrency(chatId, langCode, userStatus);

            } else if (text.equals(i18n.get(Utils.i18n.BUTTON_NOTES, langCode)) || text.equals(Action.LOCK + i18n.get(Utils.i18n.BUTTON_NOTES, langCode))) {

                buttonNotes(chatId, langCode, userStatus);

            } else if (text.equals(i18n.get(Utils.i18n.BUTTON_REMINDER, langCode)) || text.equals(Action.LOCK + i18n.get(Utils.i18n.BUTTON_REMINDER, langCode))) {

                buttonReminder(chatId, langCode, userStatus);

            } else if (text.equals(i18n.get(Utils.i18n.BUTTON_BIRTHDATE, langCode)) || text.equals(Action.LOCK + i18n.get(Utils.i18n.BUTTON_BIRTHDATE, langCode))) {

                buttonBirthdate(chatId, langCode, userStatus);

            } else if (text.equals(i18n.get(Utils.i18n.BUTTON_WALLET, langCode)) || text.equals(Action.LOCK + i18n.get(Utils.i18n.BUTTON_WALLET, langCode))) {

                buttonWallet(chatId, langCode, userStatus);

            } else if (text.equals(i18n.get(Utils.i18n.BUTTON_SETTINGS, langCode)) || text.equals(Action.LOCK + i18n.get(Utils.i18n.BUTTON_SETTINGS, langCode))) {

                buttonSettings(chatId, langCode, userStatus);

            } else if (userState.equals(UserState.AWAITING_WEATHER_LOCATION)) {

                List<SearchLocationDTO> locationDTOS = weatherApi.search(text);

                if (locationDTOS == null || locationDTOS.isEmpty()) {
                    String string = i18n.get(Utils.i18n.NOT_FOUND_CITY, langCode);
                    SendMessage sendMessage = sendMsg.sendMessage(chatId, string);
                    kundalikBot.myExecute(sendMessage);

                } else {
                    String string = i18n.get(Utils.i18n.FOUND_CITY, langCode);

                    StringBuilder sb = new StringBuilder(string);

                    for (int i = 0; i < locationDTOS.size(); i++) {
                        String str = i18n.get(Utils.i18n.ONE_CITY, langCode).formatted(i + 1, locationDTOS.get(i).getCountry(), locationDTOS.get(i).getRegion(), locationDTOS.get(i).getName());
                        sb.append(str);
                    }

                    InlineKeyboardMarkup inlineKeyboardMarkup = userInlineKeyboardService.chooseWeatherCity(locationDTOS, String.valueOf(System.currentTimeMillis()));
                    SendMessage sendMessage = sendMsg.sendMessage(chatId, sb.toString(), inlineKeyboardMarkup);
                    kundalikBot.myExecute(sendMessage);

                }
            } else if (userState.equals(UserState.AWAITING_PRAYER_LOCATION)) {

                List<SearchLocationDTO> locationDTOS = weatherApi.search(text);

                if (locationDTOS == null || locationDTOS.isEmpty()) {
                    String string = i18n.get(Utils.i18n.NOT_FOUND_CITY, langCode);
                    SendMessage sendMessage = sendMsg.sendMessage(chatId, string);
                    kundalikBot.myExecute(sendMessage);

                } else {

                    SearchLocationDTO searchLocationDTO = locationDTOS.get(0);

                    String country = searchLocationDTO.getCountry();

                    if (userStatus.equals(UserStatus.PREMIUM) || country.equals(UZBEKISTAN)) {

                        String string = i18n.get(Utils.i18n.FOUND_CITY, langCode);

                        StringBuilder sb = new StringBuilder(string);

                        for (int i = 0; i < locationDTOS.size(); i++) {
                            String str = i18n.get(Utils.i18n.ONE_CITY, langCode).formatted(i + 1, locationDTOS.get(i).getCountry(), locationDTOS.get(i).getRegion(), locationDTOS.get(i).getName());
                            sb.append(str);
                        }
                        InlineKeyboardMarkup inlineKeyboardMarkup = userInlineKeyboardService.choosePrayerCity(locationDTOS, String.valueOf(System.currentTimeMillis()));
                        SendMessage sendMessage = sendMsg.sendMessage(chatId, sb.toString(), inlineKeyboardMarkup);
                        kundalikBot.myExecute(sendMessage);

                    } else {

                        String message1 = i18n.get(Utils.i18n.FOUND_CITY_403, langCode);
                        SendMessage sendMessage = sendMsg.sendMessage(chatId, message1);
                        kundalikBot.myExecute(sendMessage);

                    }


                }

            }

        } else if (message.hasLocation()) {

            switch (userState) {
                case AWAITING_WEATHER_LOCATION -> {
                    Location location = message.getLocation();
                    WeatherResponseDTO info = weatherApi.info(location.getLatitude(), location.getLongitude());
                    String dayFormatter = generationMessageService.weatherDayFormatter(info, langCode);
                    InlineKeyboardMarkup inlineKeyboardMarkup = userInlineKeyboardService.weatherInfo(info, langCode, info.getForecast().getForecastDay().get(0).getDate());
                    kundalikBot.myExecute(sendMsg.sendMessage(chatId, dayFormatter, inlineKeyboardMarkup));
                }
                case AWAITING_PRAYER_LOCATION -> {
                    Location location = message.getLocation();
                    List<SearchLocationDTO> search = weatherApi.search(location.getLatitude() + "," + location.getLongitude());
                    if (search == null || search.isEmpty()) {
                        sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.PRAYER_NOT_FOUND, langCode));
                    } else {

                        if (userStatus.equals(UserStatus.PREMIUM)) {

                        } else {

                            SearchLocationDTO searchLocationDTO = search.get(0);

                            String country = searchLocationDTO.getCountry();
                            if (country.equals("Uzbekistan")) {

                            }

                            String region = searchLocationDTO.getRegion();
                            PrayerDayDTO prayerDayDTO = islomApi.getTodayPrayerTimes(region);
                            String prayerDayFormatter = generationMessageService.prayerDayFormatter(prayerDayDTO, langCode);
                            kundalikBot.myExecute(sendMsg.sendMessage(chatId, prayerDayFormatter));
                        }
                    }
                }
            }

        }

    }

    ///    ----------------------
    ///     COMMAND START METHODS
    ///    -----------------------
    private void commandStart(Message message, String langCode, Long chatId, UserStatus userStatus) {
        String msgText = i18n.get(Utils.i18n.WELCOME_MESSAGE, langCode).formatted(message.getFrom().getFirstName());

        ReplyKeyboardMarkup replyKeyboardMarkup = userReplyKeyboardService.welcomeMsg(langCode, userStatus);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        SendMessage sendMessage = sendMsg.sendMessage(chatId, msgText, replyKeyboardMarkup);
        kundalikBot.myExecute(sendMessage);
    }


    /// --------------------
    /// COMMAND HELP METHODS
    /// --------------------
    private void commandHelp(Long chatId, String langCode) {
        userStateService.updateUserState(chatId, UserState.COMMAND_HELP);
        SendMessage sendMessage = sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.MESSAGE_IN_DEVELOPMENT, langCode));
        kundalikBot.myExecute(sendMessage);
    }


    /// -------------------------
    /// COMMAND DASHBOARD METHODS
    /// -------------------------
    private void commandDashboard(Long chatId, String langCode) {
        TelegramUser telegramUser = telegramHelperService.telegramUser(chatId);
        if (telegramUser == null || telegramUser.getSiteUser() == null) {
            InlineKeyboardMarkup inlineKeyboardMarkup = userInlineKeyboardService.userRegisterAndLoginBtn(langCode);
            SendMessage sendMessage = sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.REGISTER_MSG, langCode), inlineKeyboardMarkup);
            kundalikBot.myExecute(sendMessage);
        } else {
            userStateService.updateUserState(chatId, UserState.BUTTON_BIRTHDATE);
            SendMessage sendMessage = sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.MESSAGE_IN_DEVELOPMENT, langCode));
            kundalikBot.myExecute(sendMessage);
        }
    }


    /// -------------------------
    /// BUTTON MAIN MENU
    /// -------------------------
    private void buttonMainMenu(Long chatId, String langCode, UserStatus userStatus) {

        String msgText = i18n.get(Utils.i18n.BUTTON_MAIN_MENU, langCode);

        ReplyKeyboardMarkup replyKeyboardMarkup = userReplyKeyboardService.welcomeMsg(langCode, userStatus);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        SendMessage sendMessage = sendMsg.sendMessage(chatId, msgText, replyKeyboardMarkup);
        kundalikBot.myExecute(sendMessage);


    }


    ///  -----------------------------------------
    ///  BUTTON WEATHER METHODS
    ///  -----------------------------------------
    private void buttonWeather(Long chatId, String langCode, UserStatus userStatus) {
//        SendMessage sendMessage = sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.MESSAGE_IN_DEVELOPMENT, langCode));
//        kundalikBot.myExecute(sendMessage);

        TelegramUser telegramUser = telegramHelperService.telegramUser(chatId);
        User user = telegramUser.getSiteUser();

        switch (userStatus) {
            case ANONYMOUS -> {
                userStateService.updateUserState(chatId, UserState.AWAITING_WEATHER_LOCATION);
                String text = i18n.get(Utils.i18n.CHOOSE_LOCATION, langCode);
                ReplyKeyboardMarkup locationMenu = userReplyKeyboardService.getLocationMenu(langCode);
                SendMessage sendMessage = sendMsg.sendMessage(chatId, text, locationMenu);
                kundalikBot.myExecute(sendMessage);
            }
            case REGISTERED -> {
                List<Address> address = user.getAddress();

                if (address == null || address.isEmpty()) {
                    userStateService.updateUserState(chatId, UserState.AWAITING_WEATHER_LOCATION);
                    String text = i18n.get(Utils.i18n.CHOOSE_LOCATION, langCode);

                    ReplyKeyboardMarkup locationMenu = userReplyKeyboardService.getLocationMenu(langCode);
                    SendMessage sendMessage = sendMsg.sendMessage(chatId, text, locationMenu);
                    kundalikBot.myExecute(sendMessage);
                } else {

                    Address address1 = address.get(0);

                    WeatherResponseDTO info = weatherApi.info(address1.getLatitude(), address1.getLongitude());

                    String dayFormatter = generationMessageService.weatherDayFormatter(info, langCode);

                    InlineKeyboardMarkup inlineKeyboardMarkup = userInlineKeyboardService.weatherInfo(info, langCode, info.getForecast().getForecastDay().get(0).getDate());

                    SendMessage sendMessage = sendMsg.sendMessage(chatId, dayFormatter, inlineKeyboardMarkup);

                    kundalikBot.myExecute(sendMessage);

                }
            }
        }


    }


    ///  -----------------------------------------
    ///  BUTTON PRAYER METHODS
    ///  -----------------------------------------
    private void buttonPrayer(Long chatId, String langCode, UserStatus userStatus) {

        TelegramUser telegramUser = telegramHelperService.telegramUser(chatId);
        User user = telegramUser.getSiteUser();

        switch (userStatus) {
            case ANONYMOUS -> {
                userStateService.updateUserState(chatId, UserState.AWAITING_PRAYER_LOCATION);
                String text = i18n.get(Utils.i18n.CHOOSE_LOCATION, langCode);
                ReplyKeyboardMarkup locationMenu = userReplyKeyboardService.getLocationMenu(langCode);
                SendMessage sendMessage = sendMsg.sendMessage(chatId, text, locationMenu);
                kundalikBot.myExecute(sendMessage);
            }
            case REGISTERED -> {
                List<Address> address = user.getAddress();

                if (address == null || address.isEmpty()) {
                    userStateService.updateUserState(chatId, UserState.AWAITING_PRAYER_LOCATION);
                    String text = i18n.get(Utils.i18n.CHOOSE_LOCATION, langCode);

                    ReplyKeyboardMarkup locationMenu = userReplyKeyboardService.getLocationMenu(langCode);
                    SendMessage sendMessage = sendMsg.sendMessage(chatId, text, locationMenu);
                    kundalikBot.myExecute(sendMessage);
                } else {

                    Address address1 = address.get(0);
                    String region = address1.getRegion();
                    PrayerDayDTO todayPrayerTimes = islomApi.getTodayPrayerTimes(region);
                    String prayerDayFormatter = generationMessageService.prayerDayFormatter(todayPrayerTimes, langCode);

                    kundalikBot.myExecute(sendMsg.sendMessage(chatId, prayerDayFormatter));

                }
            }
        }


    }


    ///  -----------------------------------------
    ///  BUTTON CURRENCY METHODS
    ///  -----------------------------------------
    private void buttonCurrency(Long chatId, String langCode, UserStatus userStatus) {
        List<CurrencyRateDTO> currencyRateDTOS = currencyApi.getAllRates();

        String currencyFormatter = generationMessageService.currencyFormatter(currencyRateDTOS, chatId, langCode, userStatus);

        SendMessage sendMessage = sendMsg.sendMessage(chatId, currencyFormatter);

        kundalikBot.myExecute(sendMessage);
    }


    ///  -----------------------------------------
    ///  BUTTON NOTES METHODS
    ///  -----------------------------------------
    private void buttonNotes(Long chatId, String langCode, UserStatus userStatus) {
        userStateService.updateUserState(chatId, UserState.BUTTON_NOTES);
        SendMessage sendMessage = sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.MESSAGE_IN_DEVELOPMENT, langCode));
        kundalikBot.myExecute(sendMessage);
    }


    ///  -----------------------------------------
    ///  BUTTON REMINDER METHODS
    ///  -----------------------------------------
    private void buttonReminder(Long chatId, String langCode, UserStatus userStatus) {
        userStateService.updateUserState(chatId, UserState.BUTTON_REMINDER);
        SendMessage sendMessage = sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.MESSAGE_IN_DEVELOPMENT, langCode));
        kundalikBot.myExecute(sendMessage);
    }


    ///  -----------------------------------------
    ///  BUTTON BIRTHDATE METHODS
    ///  -----------------------------------------
    private void buttonBirthdate(Long chatId, String langCode, UserStatus userStatus) {
        userStateService.updateUserState(chatId, UserState.BUTTON_BIRTHDATE);
        SendMessage sendMessage = sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.MESSAGE_IN_DEVELOPMENT, langCode));
        kundalikBot.myExecute(sendMessage);
    }


    ///  -----------------------------------------
    ///  BUTTON WALLET METHODS
    ///  -----------------------------------------
    private void buttonWallet(Long chatId, String langCode, UserStatus userStatus) {
        userStateService.updateUserState(chatId, UserState.BUTTON_WALLET);
        SendMessage sendMessage = sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.MESSAGE_IN_DEVELOPMENT, langCode));
        kundalikBot.myExecute(sendMessage);
    }


    ///  -----------------------------------------
    ///  BUTTON SETTINGS METHODS
    ///  -----------------------------------------
    private void buttonSettings(Long chatId, String langCode, UserStatus userStatus) {
        userStateService.updateUserState(chatId, UserState.BUTTON_SETTINGS);
//        SendMessage sendMessage = sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.MESSAGE_IN_DEVELOPMENT, langCode));
//        kundalikBot.myExecute(sendMessage);


    }
}
