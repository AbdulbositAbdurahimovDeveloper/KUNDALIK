package uz.kundalik.telegram.service.keybard.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.kundalik.telegram.payload.weather.WeatherResponseDTO;
import uz.kundalik.telegram.payload.weather.search.SearchLocationDTO;
import uz.kundalik.telegram.service.keybard.InlineKeyboardService;
import uz.kundalik.telegram.service.message.i18n;
import uz.kundalik.telegram.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.compress.java.util.jar.Pack200.Packer.FALSE;
import static org.apache.commons.compress.java.util.jar.Pack200.Packer.TRUE;
import static uz.kundalik.telegram.utils.Utils.Action.*;

@Component
@RequiredArgsConstructor
public class UserInlineKeyboardServiceImpl implements UserInlineKeyboardService {

    private final InlineKeyboardService inlineKeyboardService;
    private final i18n i18n;

    @Value("${application.prayer.default-city}")
    private String defaultCity;

    @Override
    public InlineKeyboardMarkup chooseWeatherCity(List<SearchLocationDTO> locationDTOS, String time) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int total = Math.min(locationDTOS.size(), 10); // max 10ta
        int index = 0;

        while (index < total) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            int remaining = total - index;

            // Qator uzunligini aniqlash (agar 6 yoki 7 bo‘lsa, 3/4 ga bo‘linadi)
            int rowSize;
            if (remaining <= 5) {
                rowSize = remaining;
            } else if (remaining <= 7) {
                rowSize = remaining - 3; // 6->3, 7->4
            } else {
                rowSize = 5; // 8,9,10 holatida 5 tadan bo‘linadi
            }

            for (int i = 0; i < rowSize && index < total; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(String.valueOf(index + 1));
                button.setCallbackData(String.join(":", ACTION_WEATHER, ACTION_CHOOSE, locationDTOS.get(index).getId().toString(), ACTION_TIME, time));
                row.add(button);
                index++;
            }

            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    @Override
    public InlineKeyboardMarkup choosePrayerCity(List<SearchLocationDTO> locationDTOS, String time) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int total = Math.min(locationDTOS.size(), 10); // max 10ta
        int index = 0;

        while (index < total) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            int remaining = total - index;

            // Qator uzunligini aniqlash (agar 6 yoki 7 bo‘lsa, 3/4 ga bo‘linadi)
            int rowSize;
            if (remaining <= 5) {
                rowSize = remaining;
            } else if (remaining <= 7) {
                rowSize = remaining - 3; // 6->3, 7->4
            } else {
                rowSize = 5; // 8,9,10 holatida 5 tadan bo‘linadi
            }

            for (int i = 0; i < rowSize && index < total; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(String.valueOf(index + 1));
                button.setCallbackData(String.join(":", ACTION_PRAYER, ACTION_CHOOSE, locationDTOS.get(index).getId().toString(), ACTION_TIME, time));
                row.add(button);
                index++;
            }

            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup weatherInfo(WeatherResponseDTO info, String langCode, String time) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Shahar nomini DTO'dan olamiz. Xatolikni oldini olish uchun tekshirib olamiz.
        String cityName = (info != null && info.getLocation() != null) ? info.getLocation().getName() : defaultCity;

        // --- Birinchi qator: [Soatlik] [Kunlik] ---
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton hourlyBtn = new InlineKeyboardButton();
        hourlyBtn.setText(i18n.get(Utils.i18n.BUTTON_WEATHER_HOURLY, langCode));
        hourlyBtn.setCallbackData(String.join(":", ACTION_WEATHER, ACTION_INFO, ACTION_HOURLY, cityName, ACTION_TIME, time));
        row1.add(hourlyBtn);

        InlineKeyboardButton dailyBtn = new InlineKeyboardButton();
        dailyBtn.setText(i18n.get(Utils.i18n.BUTTON_WEATHER_DAILY, langCode));
        dailyBtn.setCallbackData(String.join(":", ACTION_WEATHER, ACTION_INFO, ACTION_DAILY, cityName, ACTION_TIME, time));
        row1.add(dailyBtn);

        rows.add(row1);

        // --- Ikkinchi qator: [Yangilash] [Eslatmalar] [Boshqa shahar] ---
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        InlineKeyboardButton refreshBtn = new InlineKeyboardButton();
        refreshBtn.setText(i18n.get(Utils.i18n.BUTTON_WEATHER_REFRESH, langCode));

        refreshBtn.setCallbackData(String.join(":", ACTION_WEATHER, ACTION_INFO, ACTION_REFRESH, cityName, ACTION_TIME, time));
        row2.add(refreshBtn);

        InlineKeyboardButton reminderBtn = new InlineKeyboardButton();
        reminderBtn.setText(i18n.get(Utils.i18n.BUTTON_WEATHER_REMINDER, langCode));
        reminderBtn.setCallbackData(String.join(":", ACTION_WEATHER, ACTION_INFO, ACTION_REMINDER, cityName, ACTION_TIME, time)); // Bu premium funksiya bo'lishi mumkin
        row2.add(reminderBtn);

        InlineKeyboardButton otherCityBtn = new InlineKeyboardButton();
        otherCityBtn.setText(i18n.get(Utils.i18n.BUTTON_WEATHER_OTHER_CITY, langCode));
        otherCityBtn.setCallbackData(String.join(":", ACTION_WEATHER, ACTION_INFO, ACTION_OTHER_CITY)); // Shahar nomi shart emas, chunki bu yangi so'rovni boshlaydi
        row2.add(otherCityBtn);

        rows.add(row2);

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    @Override
    public InlineKeyboardMarkup userChooseWeatherHourly(String infoCity, boolean first, String langCode) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int startHour = first ? 1 : 13; // birinchi 1:00 dan, ikkinchi 13:00 dan
        int endHour = first ? 12 : 24;  // 12:00 gacha yoki 00:00 gacha (24 deb olamiz)

        // har qator 3 tadan soat tugmalari
        List<InlineKeyboardButton> currentRow = new ArrayList<>();
        for (int h = startHour; h <= endHour; h++) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            int displayHour = (h == 24) ? 0 : h; // 24 ni 00 sifatida ko‘rsatamiz
            String timeFormat = String.format("%02d:00", displayHour);
            btn.setText(timeFormat);
            btn.setCallbackData(String.join(":", ACTION_WEATHER, ACTION_HOURLY, ACTION_INFO, String.valueOf(timeFormat), infoCity));

            currentRow.add(btn);

            // har 3 tadan keyin yangi qator
            if (currentRow.size() == 3 || h == endHour) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            }

        }

        List<InlineKeyboardButton> paginationButton = new ArrayList<>();
        InlineKeyboardButton paginationBtn = new InlineKeyboardButton();

        InlineKeyboardButton cancelBtn = new InlineKeyboardButton();
        cancelBtn.setText("cancel");
        cancelBtn.setCallbackData(String.join(":", ACTION_WEATHER, ACTION_HOURLY, ACTION_CANCEL, TRUE, infoCity));

        if (first) {
            paginationBtn.setText("keyingi");
            paginationBtn.setCallbackData(String.join(":", ACTION_WEATHER, ACTION_HOURLY, ACTION_PAGE, TRUE, infoCity));
            paginationButton.add(cancelBtn);
            paginationButton.add(paginationBtn);
        } else {
            paginationBtn.setText("Oldingi");
            paginationBtn.setCallbackData(String.join(":", ACTION_WEATHER, ACTION_HOURLY, ACTION_PAGE, FALSE, infoCity));
            paginationButton.add(paginationBtn);
            paginationButton.add(cancelBtn);
        }


        rows.add(paginationButton);


        markup.setKeyboard(rows);
        return markup;
    }

}
