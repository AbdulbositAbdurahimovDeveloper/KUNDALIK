package uz.kundalik.site.payload.publishEvent;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import uz.kundalik.site.enums.VerificationMethod;
import uz.kundalik.site.model.User;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final String contact; // email yoki phone
    private final VerificationMethod method;

    public UserRegisteredEvent(User user, String contact, VerificationMethod method) {
        super(user);
        this.contact = contact;
        this.method = method;
    }

    @Override
    public User getSource() {
        return (User) super.getSource();
    }
}
