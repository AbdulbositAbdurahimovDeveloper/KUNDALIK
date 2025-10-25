package uz.kundalik.telegram.payload.aladhan;

import lombok.Data;

/**
 * Geografik joylashuv (koordinatalar).
 */
@Data
@SuppressWarnings("SpellCheckingInspection")
public class Location {
    private double latitude;
    private double longitude;
}