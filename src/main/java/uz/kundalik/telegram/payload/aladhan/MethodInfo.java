package uz.kundalik.telegram.payload.aladhan;

import lombok.Data;

/**
 * Hisoblash metodi haqida ma'lumot.
 */
@Data
@SuppressWarnings("SpellCheckingInspection")
public class MethodInfo {
    private int id;
    private String name;
    private MethodParams params;
    private Location location;
}