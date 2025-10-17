package uz.kundalik.site.payload.user;

import lombok.Data;

@Data
public class SimpleUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
}