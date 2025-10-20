package uz.kundalik.telegram.payload.weather.search;

import lombok.Data;
import java.util.List;

@Data
public class LocationResponseDTO {
    private List<SearchLocationDTO> locations;
}
