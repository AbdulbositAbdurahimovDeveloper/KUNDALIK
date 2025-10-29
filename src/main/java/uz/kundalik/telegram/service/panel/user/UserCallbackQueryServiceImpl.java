package uz.kundalik.telegram.service.panel.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.kundalik.telegram.controller.KundalikBot;
import uz.kundalik.telegram.enums.UserState;
import uz.kundalik.telegram.enums.UserStatus;
import uz.kundalik.telegram.model.TelegramUser;
import uz.kundalik.telegram.payload.aladhan.PrayerData;
import uz.kundalik.telegram.payload.prayer.PrayerDayDTO;
import uz.kundalik.telegram.payload.weather.WeatherResponseDTO;
import uz.kundalik.telegram.payload.weather.forecast.ForecastDTO;
import uz.kundalik.telegram.payload.weather.forecast.ForecastDayDTO;
import uz.kundalik.telegram.payload.weather.forecast.HourDTO;
import uz.kundalik.telegram.payload.weather.search.SearchLocationDTO;
import uz.kundalik.telegram.service.TelegramHelperService;
import uz.kundalik.telegram.service.api.AladhanApi;
import uz.kundalik.telegram.service.api.IslomApi;
import uz.kundalik.telegram.service.api.WeatherApi;
import uz.kundalik.telegram.service.keybard.user.UserInlineKeyboardService;
import uz.kundalik.telegram.service.message.GenerationMessageService;
import uz.kundalik.telegram.service.message.SendMsg;
import uz.kundalik.telegram.service.message.i18n;
import uz.kundalik.telegram.utils.Utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static uz.kundalik.telegram.utils.Utils.Action.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCallbackQueryServiceImpl implements UserCallbackQueryService {

    private final KundalikBot kundalikBot;
    private final TelegramHelperService telegramHelperService;
    private final SendMsg sendMsg;
    private final i18n i18n;
    private final WeatherApi weatherApi;
    private final GenerationMessageService generationMessageService;
    private final IslomApi islomApi;
    private final AladhanApi aladhanApi;
    private final UserInlineKeyboardService userInlineKeyboardService;

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {

        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long chatId = callbackQuery.getMessage().getChatId();
        String queryData = callbackQuery.getData();
        String callbackQueryId = callbackQuery.getId();

        UserState userState = telegramHelperService.userState(chatId);
        String langCode = telegramHelperService.langCode(chatId);
        UserStatus userStatus = telegramHelperService.userStatus(chatId);
        TelegramUser telegramUser = telegramHelperService.telegramUser(chatId);

        kundalikBot.myExecute(new AnswerCallbackQuery(callbackQuery.getId()));

        try {
            String[] split = queryData.split(":");
            String prefix = split[0];

            switch (prefix) {
                case ACTION_WEATHER -> {

                    String action = split[1];
                    switch (action) {
                        case ACTION_CHOOSE -> {
                            long cityId = Long.parseLong(split[2]);
                            List<SearchLocationDTO> search = weatherApi.search(ID + cityId);
                            SearchLocationDTO searchLocationDTO = search.get(0);

                            WeatherResponseDTO info = weatherApi.info(searchLocationDTO.getLat(), searchLocationDTO.getLon());
                            String dayFormatter = generationMessageService.weatherDayFormatter(info, langCode);
                            InlineKeyboardMarkup inlineKeyboardMarkup = userInlineKeyboardService.weatherInfo(info, langCode, split[4]);
                            EditMessageText editMessageText = sendMsg.editMessage(chatId, messageId, dayFormatter, inlineKeyboardMarkup);
                            kundalikBot.myExecute(editMessageText);
                        }
                        case ACTION_INFO -> {

                            String infoAction = split[2];
                            String infoCity = split[3];

                            switch (infoAction) {
                                case ACTION_HOURLY -> {

                                    Instant instant = Instant.ofEpochSecond(callbackQuery.getMessage().getDate());
                                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());


                                    String formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                    boolean first = localDateTime.getHour() < 12;

                                    InlineKeyboardMarkup inlineKeyboardMarkup = userInlineKeyboardService.userChooseWeatherHourly(infoCity, first, langCode);

                                    EditMessageText editMessageText = sendMsg.editMessage(chatId, messageId,
                                            i18n.get(Utils.i18n.CHOOSE_WEATHER_TIMING, langCode).formatted(formattedDate),
                                            inlineKeyboardMarkup);

                                    kundalikBot.myExecute(editMessageText);


                                }
                                case ACTION_DAILY -> {

                                }
                                case ACTION_REFRESH -> {
                                    String alertText = i18n.get(Utils.i18n.ALERT_PREMIUM_FOR_REFRESH, langCode);
                                    AnswerCallbackQuery answerCallbackQuery = sendMsg.answerCallbackQuery(callbackQueryId, alertText, false);
                                    kundalikBot.myExecute(answerCallbackQuery);

                                }
                                case ACTION_REMINDER -> {

                                }
                                case ACTION_OTHER_CITY -> {

                                }
                            }

                        }
                        case ACTION_HOURLY -> {

                            String pageAndCancel = split[2];
                            switch (pageAndCancel) {

                                case ACTION_INFO -> {
                                    String infoCity = split[5];
                                    WeatherResponseDTO info = weatherApi.info(infoCity);
                                    ForecastDTO forecastDTO = info.getForecast();

                                    final String targetTime = split[3] + ":" + split[4]; // Natija: "13:00"

                                    final String todayDate = java.time.LocalDate.now().toString();
                                    List<ForecastDayDTO> forecastList = forecastDTO.getForecastDay();

                                    Optional<HourDTO> foundHourOptional = forecastList.stream()
                                            // Birinchi qadam: Bugungi kunga to'g'ri keladigan Forecast obyektini topish
                                            .filter(forecast -> forecast.getDate().equals(todayDate))
                                            .findFirst()
                                            // Ikkinchi qadam: Agar Forecast topilsa, uning ichidagi Hour listidan kerakli soatni qidirish
                                            .flatMap(todayForecast -> todayForecast.getHour().stream()
                                                    .filter(hour -> hour.getTime().endsWith(" " + targetTime)) // Masalan, "... 13:00" bilan tugashini tekshirish
                                                    .findFirst()
                                            );

                                    if (foundHourOptional.isPresent()) {
                                        HourDTO hourDTO = foundHourOptional.get();
                                        System.out.println(hourDTO);

                                        String formatted = i18n.get(Utils.i18n.INFO_HOURLY_WEATHER, langCode).formatted(
                                                hourDTO.getTime(),                // 🕒 vaqt
                                                hourDTO.getTempC(),               // harorat
                                                hourDTO.getFeelslikeC(),          // his qilinishi
                                                hourDTO.getCondition().getText(), // ob-havo matni
                                                hourDTO.getHumidity(),            // namlik
                                                hourDTO.getWindKph(),             // shamol tezligi
                                                hourDTO.getWindDir(),             // shamol yo‘nalishi
                                                hourDTO.getWindDegree(),          // shamol burchagi
                                                hourDTO.getChanceOfRain(),        // yomg‘ir ehtimoli
                                                hourDTO.getChanceOfSnow(),        // qor ehtimoli
                                                hourDTO.getCloud(),               // bulutlilik
                                                hourDTO.getUv()                   // UV darajasi
                                        );
                                        EditMessageText editMessageText = sendMsg.editMessage(chatId, messageId, formatted);
                                        kundalikBot.myExecute(editMessageText);
                                    }

                                }
                                case ACTION_PAGE -> {

                                    Instant instant = Instant.ofEpochSecond(callbackQuery.getMessage().getDate());
                                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

                                    String formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                    boolean first = localDateTime.getHour() < 12;

                                    InlineKeyboardMarkup inlineKeyboardMarkup = userInlineKeyboardService.userChooseWeatherHourly(split[4], !Boolean.parseBoolean(split[3]), langCode);

                                    EditMessageText editMessageText = sendMsg.editMessage(chatId, messageId,
                                            i18n.get(Utils.i18n.CHOOSE_WEATHER_TIMING, langCode).formatted(formattedDate),
                                            inlineKeyboardMarkup);

                                    kundalikBot.myExecute(editMessageText);

                                }
                                case ACTION_CANCEL -> {

                                    List<SearchLocationDTO> search = weatherApi.search(split[4]);
                                    SearchLocationDTO searchLocationDTO = search.get(0);

                                    WeatherResponseDTO info = weatherApi.info(searchLocationDTO.getLat(), searchLocationDTO.getLon());
                                    String dayFormatter = generationMessageService.weatherDayFormatter(info, langCode);
                                    InlineKeyboardMarkup inlineKeyboardMarkup = userInlineKeyboardService.weatherInfo(info, langCode, split[4]);
                                    EditMessageText editMessageText = sendMsg.editMessage(chatId, messageId, dayFormatter, inlineKeyboardMarkup);
                                    kundalikBot.myExecute(editMessageText);

                                }


                            }


                        }
                    }

                }
                case ACTION_PRAYER -> {
                    long cityId = Long.parseLong(split[2]);
                    List<SearchLocationDTO> search = weatherApi.search(ID + cityId);
                    SearchLocationDTO searchLocationDTO = search.get(0);

                    if (userStatus.equals(UserStatus.ANONYMOUS)) {

                        PrayerDayDTO prayerTimes = islomApi.getTodayPrayerTimes(searchLocationDTO.getRegion());
                        String dayFormatter = generationMessageService.prayerDayFormatter(prayerTimes, langCode);
                        EditMessageText editMessageText = sendMsg.editMessage(chatId, messageId, dayFormatter);
                        kundalikBot.myExecute(editMessageText);

                    } else {

//
                        String formattedDate = LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(callbackQuery.getMessage().getDate()),
                                ZoneId.systemDefault()
                        ).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                        PrayerData timingsByCityAndDate = aladhanApi.getTimingsByCityAndDate(formattedDate, searchLocationDTO.getName(), searchLocationDTO.getCountry());
                        String s = generationMessageService.formatForPremiumUser(timingsByCityAndDate, langCode, searchLocationDTO.getName(), LocalTime.now());
                        SendMessage sendMessage = sendMsg.sendMessage(chatId, s);
                        kundalikBot.myExecute(sendMessage);


                    }
                }
            }

        } catch (Exception e) {
            log.error("Callbackni qayta ishlashda xatolik yuz berdi: Query='{}'", queryData, e);
            kundalikBot.myExecute(sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.ERROR_MESSAGE, langCode)));
        }


    }
}
