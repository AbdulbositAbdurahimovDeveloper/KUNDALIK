package uz.kundalik.telegram.service.panel.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.kundalik.telegram.controller.KundalikBot;
import uz.kundalik.telegram.enums.UserState;
import uz.kundalik.telegram.enums.UserStatus;
import uz.kundalik.telegram.model.TelegramUser;
import uz.kundalik.telegram.payload.weather.WeatherResponseDTO;
import uz.kundalik.telegram.payload.weather.search.SearchLocationDTO;
import uz.kundalik.telegram.service.TelegramHelperService;
import uz.kundalik.telegram.service.api.WeatherApi;
import uz.kundalik.telegram.service.message.GenerationMessageService;
import uz.kundalik.telegram.service.message.SendMsg;
import uz.kundalik.telegram.service.message.i18n;
import uz.kundalik.telegram.utils.Utils;

import java.util.List;

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

                    long cityId = Long.parseLong(split[2]);
                    List<SearchLocationDTO> search = weatherApi.search(ID + cityId);
                    SearchLocationDTO searchLocationDTO = search.get(0);

                    WeatherResponseDTO info = weatherApi.info(searchLocationDTO.getLat(), searchLocationDTO.getLon());
                    String dayFormatter = generationMessageService.weatherDayFormatter(info, langCode);
                    EditMessageText editMessageText = sendMsg.editMessage(chatId, messageId, dayFormatter);
                    kundalikBot.myExecute(editMessageText);
                }
            }

        } catch (Exception e) {
            log.error("Callbackni qayta ishlashda xatolik yuz berdi: Query='{}'", queryData, e);
            kundalikBot.myExecute(sendMsg.sendMessage(chatId, i18n.get(Utils.i18n.ERROR_MESSAGE, langCode)));
        }


    }
}
