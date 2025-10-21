package uz.kundalik.site.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.kundalik.site.enums.Gender;
import uz.kundalik.site.enums.Role;
import uz.kundalik.site.model.User;
import uz.kundalik.site.model.UserProfile;
import uz.kundalik.site.repository.UserRepository;

import java.time.LocalDate;

/**
 * Initializes the database with default system users on application startup.
 * <p>
 * This component interacts directly with the {@link UserRepository} and {@link PasswordEncoder}
 * to create system users, bypassing the main {@code AuthService}. This approach is used
 * to keep the data seeding logic separate from the application's business logic.
 * <p>
 * It ensures that each default user has a unique phone number to comply with the
 * partial unique index on the user_profiles table.
 * Each user creation is an isolated process; a failure in one will not prevent others.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        User user1 = userRepository.findByEmail("abdulbositabdurahimov260@gmail.com").orElse(null);
        if (user1 == null) {

            User user = new User();
            user.setEmail("abdulbositabdurahimov260@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));
            user.setEnabled(true);
            user.setRole(Role.ADMIN);
            user.setProfile(new UserProfile(
                    "Abdulbosit",
                    "Abdurahimov",
                    Gender.MALE,
                    LocalDate.of(2006, 3, 25),
                    "+998931968377",
                    null,
                    user
            ));

            userRepository.save(user);

            System.out.println("User Created");
        }


    }
}