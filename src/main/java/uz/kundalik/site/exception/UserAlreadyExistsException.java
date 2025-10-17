package uz.kundalik.site.exception;

import org.jetbrains.annotations.NotNull;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(@NotNull String message) {
        super(message);
    }
}
