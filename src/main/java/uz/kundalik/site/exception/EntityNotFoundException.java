package uz.kundalik.site.exception;

import org.jetbrains.annotations.NotNull;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(@NotNull String message) {
        super(message);
    }
}
