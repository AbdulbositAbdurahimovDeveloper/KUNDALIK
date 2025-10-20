package uz.kundalik.telegram.service.keybard.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.kundalik.telegram.payload.weather.search.SearchLocationDTO;
import uz.kundalik.telegram.service.keybard.InlineKeyboardService;

import java.util.ArrayList;
import java.util.List;

import static uz.kundalik.telegram.utils.Utils.Action.*;

@Component
@RequiredArgsConstructor
public class UserInlineKeyboardServiceImpl implements UserInlineKeyboardService {

    private final InlineKeyboardService inlineKeyboardService;

    @Override
    public InlineKeyboardMarkup chooseCity(List<SearchLocationDTO> locationDTOS) {
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
                button.setCallbackData(String.join(":", ACTION_WEATHER, ACTION_CHOOSE, locationDTOS.get(index).getId().toString()));
                row.add(button);
                index++;
            }

            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

}
