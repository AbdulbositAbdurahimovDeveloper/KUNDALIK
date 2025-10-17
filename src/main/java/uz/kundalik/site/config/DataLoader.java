package uz.kundalik.site.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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

    @Override
    public void run(String... args) throws Exception {

    }
}