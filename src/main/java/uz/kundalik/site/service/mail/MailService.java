package uz.kundalik.site.service.mail;

import org.springframework.scheduling.annotation.Async;
import uz.kundalik.site.model.User;

public interface MailService {
    /**
     * Sends an account verification email to a newly registered user.
     * This operation can be time-consuming and should be executed asynchronously.
     *
     * @param user  The user who needs to verify their account.
     * @param token The unique verification token generated for the user.
     */
    void sendVerificationEmail(User user, String token);

    void sendPasswordResetEmail(User user, String token);

    @Async
    void sendEmailChangeConfirmationEmail(User user, String newEmail, String token);
}