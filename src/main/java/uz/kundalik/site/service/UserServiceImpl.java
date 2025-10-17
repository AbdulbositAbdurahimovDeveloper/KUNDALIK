package uz.kundalik.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.kundalik.site.enums.TokenType;
import uz.kundalik.site.exception.*;
import uz.kundalik.site.mapper.UserMapper;
import uz.kundalik.site.model.Attachment;
import uz.kundalik.site.model.User;
import uz.kundalik.site.model.UserProfile;
import uz.kundalik.site.model.VerificationToken;
import uz.kundalik.site.payload.response.MessageResponseDTO;
import uz.kundalik.site.payload.user.*;
import uz.kundalik.site.repository.AttachmentRepository;
import uz.kundalik.site.repository.UserRepository;
import uz.kundalik.site.repository.VerificationTokenRepository;
import uz.kundalik.site.service.generate.GenerateUniqueService;
import uz.kundalik.site.service.mail.MailService;
import uz.kundalik.site.service.sms.SmsService;
import uz.kundalik.site.service.template.AttachmentService;
import uz.kundalik.site.service.template.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final GenerateUniqueService generateUniqueService;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final MailService mailService;
    private final SmsService smsService;
    private final UserMapper userMapper;

    private static final int MAX_TOKEN_GENERATION_ATTEMPTS = 10;

    @Value("${application.security.verification.max-attempts:3}")
    Integer MAX_ATTEMPTS;
    @Value("${application.security.verification.lockout-duration-minutes:30}")
    Integer LOCKOUT_DURATION_MINUTES;

    /**
     * Loads a user by their username and returns a {@link UserDetails} object.
     * <p>
     * This method is annotated with {@code @Cacheable}. This means:
     * <ul>
     *   <li>The first time this method is called for a specific username (e.g., "john.doe"),
     *       it will execute the method body, fetch the user from the database, and store the
     *       result in the cache named "orom_users" with the username as the key.</li>
     *   <li>On subsequent calls with the same username, the method body will be skipped entirely.
     *       Spring will directly return the user object from the cache, avoiding a database hit.</li>
     * </ul>
     * The {@code unless="#result == null"} condition prevents caching of null results,
     * which is important to allow a user who was not found before to be found later if they register.
     *
     * @param email The username identifying the user whose data is required.
     * @return A fully populated {@link UserDetails} object (our {@code User} entity implements this).
     * @throws UsernameNotFoundException if the user could not be found.
     */
    @Override
    @Transactional(readOnly = true)
//    @Cacheable(value = CacheNames.USERS, key = "#email", unless = "#result == null")
//    @Cacheable(value = CacheNames.USERS, key = "#email", unless = "#result == null", condition = "#email != null and #email.length() > 0")
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return getUser(email);
    }


    /**
     * Retrieves the complete profile information for the currently authenticated user.
     *
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return A DTO containing the user's detailed information.
     * @throws EntityNotFoundException if the authenticated user cannot be found.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUserProfile(User currentUser) {
        User user = getUser(currentUser.getUsername());
        return userMapper.toUserResponseDTO(user);
    }

    /**
     * Updates the profile information (e.g., first name, last name) of the currently authenticated user.
     * This method supports partial updates; null fields in the request DTO will be ignored.
     *
     * @param requestDTO  The DTO with the fields to update.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return The updated user profile DTO.
     */
    @Override
    public UserResponseDTO updateCurrentUserProfile(UserUpdateRequestDTO requestDTO, User currentUser) {
        User user = getUser(currentUser.getUsername());

        UserProfile profile = user.getProfile();

        if (requestDTO.getFirstName() != null) {
            profile.setFirstName(requestDTO.getFirstName());
        }
        if (requestDTO.getLastName() != null) {
            profile.setLastName(requestDTO.getLastName());
        }
        if (requestDTO.getGender() != null) {
            profile.setGender(requestDTO.getGender());
        }
        if (requestDTO.getBirthDate() != null) {
            profile.setBirthDate(requestDTO.getBirthDate());
        }

        user.setProfile(profile);
        User saved = userRepository.save(user);

        return userMapper.toUserResponseDTO(saved);
    }

    /**
     * Uploads or updates the profile picture for the currently authenticated user.
     *
     * @param file        The image file to be uploaded.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return A success message, possibly including the URL of the new picture.
     * @throws FileUploadException if the file upload fails.
     */
    @Override
    public MessageResponseDTO uploadProfilePicture(MultipartFile file, User currentUser) {
        User user = getUser(currentUser.getUsername());

        UserProfile profile = user.getProfile();
        Attachment attachment = attachmentService.saveImage(file);
        profile.setProfilePicture(attachment);
        user.setProfile(profile);
        userRepository.save(user);

        return new MessageResponseDTO("User profile has been uploaded successfully");
    }


    /**
     * Changes the password for the currently authenticated user after verifying their current password.
     *
     * @param requestDTO  The DTO containing the current and new passwords.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return A success message.
     * @throws InvalidPasswordException if the current password does not match.
     */
    @Override
    public MessageResponseDTO changePassword(ChangePasswordDTO requestDTO, User currentUser) {

        User user = getUser(currentUser.getUsername());

        String hashPassword = user.getPassword();
        if (passwordEncoder.matches(requestDTO.getCurrentPassword(), hashPassword)) {
            user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
            userRepository.save(user);
            return new MessageResponseDTO("Password has been changed successfully");
        }

        return new MessageResponseDTO("Wrong password");
    }

    /**
     * Initiates the process to change the email for the currently authenticated user.
     * It sends a confirmation link to the proposed new email address.
     *
     * @param requestDTO  The DTO containing the new email and current password for verification.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return A message indicating that a confirmation link has been sent.
     * @throws InvalidPasswordException    if the current password is incorrect.
     * @throws EmailAlreadyExistsException if the new email is already in use.
     */
    @Override
    @Transactional
    public MessageResponseDTO requestEmailChange(EmailChangeRequestDTO requestDTO, User currentUser) {
        if (!passwordEncoder.matches(requestDTO.getCurrentPassword(), currentUser.getPassword())) {
            throw new InvalidPasswordException("Incorrect current password.");
        }

        if (userRepository.existsByEmail(requestDTO.getNewEmail())) {
            throw new EmailAlreadyExistsException("This email address is already in use by another account.");
        }

        checkAndIncrementVerificationAttempts(currentUser);

        String tokenString = UUID.randomUUID().toString();

        verificationTokenRepository.deleteByUserAndTokenType(currentUser, TokenType.EMAIL_CHANGE);

        VerificationToken emailChangeToken = new VerificationToken();
        emailChangeToken.setToken(tokenString);
        emailChangeToken.setUser(currentUser);
        emailChangeToken.setTokenType(TokenType.EMAIL_CHANGE);
        emailChangeToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        try {
            String metadataJson = objectMapper.writeValueAsString(Map.of("newEmail", requestDTO.getNewEmail()));
            emailChangeToken.setMetadata(metadataJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize metadata for email change", e);
            throw new RuntimeException("Internal server error while processing request.");
        }

        verificationTokenRepository.save(emailChangeToken);

        mailService.sendEmailChangeConfirmationEmail(currentUser, requestDTO.getNewEmail(), tokenString);

        return new MessageResponseDTO("A confirmation link has been sent to your new email address. Please check your inbox.");
    }


    /**
     * Resends the email verification link if the user's email is currently unverified.
     *
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account. The user will no longer be able to log in.
     * @return A success message indicating the email has been resent.
     * @throws DataConflictException if the user does not have an email or if it's already verified.
     */
    @Override
    @Transactional
    public MessageResponseDTO resendEmailVerification(User currentUser) {
        if (currentUser.getEmail() == null || currentUser.getEmail().isBlank()) {
            throw new BadRequestException("You do not have an email address associated with your account.");
        }
        if (currentUser.isEnabled()) {
            throw new BadRequestException("Your email address is already verified.");
        }

        checkAndIncrementVerificationAttempts(currentUser);

        verificationTokenRepository.deleteByUserAndTokenType(currentUser, TokenType.ACCOUNT_ACTIVATION);

        String verificationCode = generateUniqueService.generateUniqueSixDigitToken(TokenType.ACCOUNT_ACTIVATION);

        createAndSaveToken(currentUser, verificationCode, TokenType.ACCOUNT_ACTIVATION);

        mailService.sendVerificationEmail(currentUser, verificationCode);

        return new MessageResponseDTO("A new verification email has been sent to " + currentUser.getEmail());
    }


    /**
     * Deactivates (soft-deletes) the account of the currently authenticated user.
     *
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     *                    The user will no longer be able to log in.
     */
    @Override
    public void deleteMyAccount(User currentUser) {

    }

    private User getUser(String userIdAsString) {

        return userRepository.findByEmail(userIdAsString)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + userIdAsString));
    }


    /**
     * Generates a secure-ish 6-digit number as a String (100000..999999).
     */
    private String generateSixDigitCode() {
        int number = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
        return Integer.toString(number);
    }

    /**
     * A private helper method to check for rate limiting and abuse prevention.
     * It checks if the user has exceeded the maximum number of verification requests.
     * If the lockout period has passed, it resets the counter. Otherwise, it throws an exception.
     * If the user is allowed to proceed, it increments their attempt count.
     *
     * @param user The user to check.
     */
    private void checkAndIncrementVerificationAttempts(User user) {
        LocalDateTime now = LocalDateTime.now();

        if (user.getVerificationCodeRequestCount() >= MAX_ATTEMPTS) {
            LocalDateTime lockoutEndTime = user.getLastVerificationCodeRequestAt().plusMinutes(LOCKOUT_DURATION_MINUTES);

            if (lockoutEndTime.isAfter(now)) {
                long minutesRemaining = ChronoUnit.MINUTES.between(now, lockoutEndTime);
                throw new TooManyRequestsException("You have exceeded the maximum number of requests. Please try again in " + (minutesRemaining + 1) + " minutes.");
            } else {
                // Reset the counter if the lockout period has expired
                user.setVerificationCodeRequestCount(0);
            }
        }

        // Increment the counter and update the timestamp
        user.setVerificationCodeRequestCount(user.getVerificationCodeRequestCount() + 1);
        user.setLastVerificationCodeRequestAt(now);
        userRepository.save(user);
    }

    private void createAndSaveToken(User user, String token, TokenType type) {
        VerificationToken newToken = new VerificationToken();
        newToken.setToken(token);
        newToken.setUser(user);
        newToken.setTokenType(type);
        newToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        verificationTokenRepository.save(newToken);
    }
}
