package uz.kundalik.telegram.payload.aladhan;

import lombok.Data;

/**
 * Taqvim turi (masalan, "AD" yoki "AH").
 */
@Data
@SuppressWarnings("SpellCheckingInspection")
public class Designation {
    private String abbreviated;
    private String expanded;
}