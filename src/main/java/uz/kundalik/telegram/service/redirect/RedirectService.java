package uz.kundalik.telegram.service.redirect;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface RedirectService {

    void adminRedirect(Update update);

    void userRedirect(Update update);
}
