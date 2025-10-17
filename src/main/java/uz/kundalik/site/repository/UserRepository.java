package uz.kundalik.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.kundalik.site.exception.EntityNotFoundException;
import uz.kundalik.site.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    default User findByIdOrThrowException(Long userId) {
        return findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    Optional<User> findByEmailAndDeletedIsFalse(String email);

    Optional<User> findByIdAndEnabledTrue(Long id);


    Optional<User> findByEmailAndEnabledFalseAndDeletedFalse(String email);

    Optional<User> findByEmail(String email);


}