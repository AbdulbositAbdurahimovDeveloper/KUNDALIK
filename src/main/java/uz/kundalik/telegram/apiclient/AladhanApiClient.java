package uz.kundalik.telegram.apiclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uz.kundalik.telegram.payload.aladhan.AnnualCalendarResponse;
import uz.kundalik.telegram.payload.aladhan.CalendarResponse;
import uz.kundalik.telegram.payload.aladhan.PrayerTimeResponse;

/**
 * Feign client for interacting with the Aladhan Prayer Times API (api.aladhan.com).
 * This client provides methods to fetch daily, monthly, and annual prayer times.
 */
@FeignClient(name = "aladhan-api", url = "${application.aladhan.api.base-url}")
public interface AladhanApiClient {

    // =======================================================================================
    // == Daily Timings
    // =======================================================================================

    /**
     * Fetches daily prayer times for a specific date.
     * The API endpoint is: /v1/timingsByCity/{date}
     *
     * @param date   The date in DD-MM-YYYY format (e.g., "22-10-2025").
     * @param city   The name of the city (e.g., "Tashkent").
     * @param country The name of the country (e.g., "Uzbekistan").
     * @param method The calculation method ID (e.g., 3 for Muslim World League).
     * @return A {@link PrayerTimeResponse} object containing the timings for the specified day.
     */
    @GetMapping("/timingsByCity/{date}")
    PrayerTimeResponse getTimingsByCityAndDate(
            @PathVariable("date") String date,
            @RequestParam("city") String city,
            @RequestParam("country") String country,
            @RequestParam("method") int method
    );

    /**
     * Fetches daily prayer times for the current date.
     * The API endpoint is: /v1/timingsByCity
     *
     * @param city   The name of the city.
     * @param country The name of the country.
     * @param method The calculation method ID.
     * @return A {@link PrayerTimeResponse} object containing the timings for the current day.
     */
    @GetMapping("/timingsByCity")
    PrayerTimeResponse getTimingsByCityForToday(
            @RequestParam("city") String city,
            @RequestParam("country") String country,
            @RequestParam("method") int method
    );

    // =======================================================================================
    // == Calendar Timings (Weekly, Monthly, Annual)
    // =======================================================================================

    /**
     * Fetches a monthly prayer times calendar for a specific month and year.
     * This method can be used to derive weekly timings by processing its response.
     * The API endpoint is: /v1/calendarByCity/{year}/{month}
     *
     * @param year    The calendar year (e.g., 2025).
     * @param month   The calendar month (1-12).
     * @param city    The name of the city.
     * @param country The name of the country.
     * @param method  The calculation method ID.
     * @return A {@link CalendarResponse} object containing a list of daily prayer data for the entire month.
     */
    @GetMapping("/calendarByCity/{year}/{month}")
    CalendarResponse getMonthlyCalendarByCity(
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @RequestParam("city") String city,
            @RequestParam("country") String country,
            @RequestParam("method") int method
    );

    /**
     * Fetches an annual prayer times calendar for a specific year.
     * This is the most efficient way to get data for longer periods (like weeks or months).
     * The API endpoint is: /v1/calendarByCity/{year}?annual=true
     *
     * @param year    The calendar year (e.g., 2025).
     * @param city    The name of the city.
     * @param country The name of the country.
     * @param method  The calculation method ID.
     * @param annual  Must be true to fetch the annual calendar. Default is true.
     * @return An {@link AnnualCalendarResponse} object containing a map of all months and their respective daily prayer data.
     */
    @GetMapping("/calendarByCity/{year}")
    AnnualCalendarResponse getAnnualCalendarByCity(
            @PathVariable("year") int year,
            @RequestParam("city") String city,
            @RequestParam("country") String country,
            @RequestParam("method") int method,
            @RequestParam(name = "annual", defaultValue = "true") boolean annual
    );
}