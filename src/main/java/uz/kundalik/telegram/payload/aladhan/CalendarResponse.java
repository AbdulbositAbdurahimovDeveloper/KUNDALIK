package uz.kundalik.telegram.payload.aladhan;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CalendarResponse {
    private int code;
    private String status;
    
    // Oylik javob uchun `List` ishlatiladi
    private List<PrayerData> data; 
    
    // Yillik javob uchun `Map` ishlatiladi (oy raqami -> kunlar ro'yxati)
    // private Map<String, List<PrayerData>> data; // Agar yillik javob shunday kelsa
}