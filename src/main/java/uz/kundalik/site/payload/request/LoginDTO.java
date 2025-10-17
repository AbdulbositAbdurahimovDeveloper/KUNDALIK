package uz.kundalik.site.payload.request;

import lombok.Data;

@Data
public class LoginDTO {

    private String email;
    private String password;
}
