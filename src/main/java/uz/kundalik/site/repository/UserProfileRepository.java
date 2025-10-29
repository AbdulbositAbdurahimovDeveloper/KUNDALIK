package uz.kundalik.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.kundalik.site.model.UserProfile;

import java.time.LocalDate;
import java.util.List;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    List<UserProfile> findAllByBirthDate(LocalDate birthDate);
}