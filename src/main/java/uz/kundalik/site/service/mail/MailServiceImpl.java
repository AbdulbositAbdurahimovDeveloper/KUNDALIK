package uz.kundalik.site.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import uz.kundalik.site.model.User;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${application.site.frontend-base-url}")
    private String frontendBaseUrl;

    @Value("${application.site.backend-base-url}")
    private String backendBaseUrl;

    /**
     * {@inheritDoc}
     * YANGILANGAN VERSIYA: Bu implementatsiya endi akkauntni tasdiqlash uchun
     * 6 xonali kod yuboradi. "email-verification" shablonidan foydalaniladi.
     */
    @Override
    @Async
    public void sendVerificationEmail(User user, String verificationCode) { // `token` nomi `verificationCode`'ga o'zgartirildi
        String subject = "Your Verification Code | Oromland";

        // Shablonga endi havola o'rniga kodni uzatamiz
        Map<String, Object> templateModel = Map.of(
                "name", user.getProfile().getFirstName(),
                "verificationCode", verificationCode, // O'zgarish shu yerda
                "expirationTime", "10 minutes" // Kodlar odatda qisqa yashaydi
        );

        sendHtmlEmail(user.getEmail(), subject, "email-verification", templateModel);
    }

    /**
     * {@inheritDoc}
     * O'ZGARISHSIZ: Bu implementatsiya parolni tiklash uchun frontend'ga yo'naltiruvchi
     * havola yuboradi. "password-reset" shablonidan foydalaniladi.
     */
    @Override
    @Async
    public void sendPasswordResetEmail(User user, String token) {
        String subject = "Reset Your Password | Oromland";
        String resetLink = frontendBaseUrl + "/reset-password?token=" + token;

        Map<String, Object> templateModel = Map.of(
                "name", user.getProfile().getFirstName(),
                "actionLink", resetLink,
                "expirationTime", "15 minutes" // Parol tiklash linklari ham qisqa yashashi kerak
        );

        sendHtmlEmail(user.getEmail(), subject, "password-reset", templateModel);
    }

    @Async
    @Override
    public void sendEmailChangeConfirmationEmail(User user, String newEmail, String token) {
        String subject = "Confirm Your New Email Address | Oromland";

        // Bu link bosilganda backend'ga so'rov yuboradi, u esa o'z navbatida frontend'ga redirect qiladi.
        String successRedirectUrl = frontendBaseUrl + "/profile/settings?email_change=success";
        String confirmationLink = backendBaseUrl + "/api/v1/auth/confirm-email-change?token=" + token + "&redirect_url=" + successRedirectUrl;

        Map<String, Object> templateModel = Map.of(
                "name", user.getProfile().getFirstName(),
                "actionLink", confirmationLink,
                "expirationTime", "15 minutes"
        );

        // DIQQAT: Xat mavjud emailga emas, balki tasdiqlanishi kerak bo'lgan YANGI emailga yuboriladi.
        sendHtmlEmail(newEmail, subject, "email-change-confirmation", templateModel);
    }

    /**
     * HTML email yuborish uchun universal yordamchi metod. (O'zgarishsiz)
     */
    private void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {
        try {
            log.info("Attempting to send '{}' email to {}", subject, to);
            Context context = new Context();
            context.setVariables(templateModel);
            String htmlContent = templateEngine.process(templateName, context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
            log.info("Email '{}' successfully sent to {}", subject, to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}. Subject: '{}'. Reason: {}", to, subject, e.getMessage(), e);
        }
    }
}