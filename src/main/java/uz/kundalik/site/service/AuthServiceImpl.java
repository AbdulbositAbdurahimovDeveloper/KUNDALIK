package uz.kundalik.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.kundalik.site.config.security.JwtService;
import uz.kundalik.site.enums.Role;
import uz.kundalik.site.enums.TokenType;
import uz.kundalik.site.enums.VerificationMethod;
import uz.kundalik.site.exception.*;
import uz.kundalik.site.model.User;
import uz.kundalik.site.model.UserProfile;
import uz.kundalik.site.model.VerificationToken;
import uz.kundalik.site.payload.publishEvent.UserRegisteredEvent;
import uz.kundalik.site.payload.request.LoginDTO;
import uz.kundalik.site.payload.request.RefreshTokenRequestDTO;
import uz.kundalik.site.payload.request.ResendVerificationRequestDTO;
import uz.kundalik.site.payload.request.ResetPasswordRequestDTO;
import uz.kundalik.site.payload.response.MessageResponseDTO;
import uz.kundalik.site.payload.response.TokenDTO;
import uz.kundalik.site.payload.user.ForgotPasswordRequestDTO;
import uz.kundalik.site.payload.user.UserRegisterRequestDTO;
import uz.kundalik.site.payload.user.UserRegisterResponseDTO;
import uz.kundalik.site.payload.user.VerifyCodeRequestDTO;
import uz.kundalik.site.properties.JwtProperties;
import uz.kundalik.site.repository.UserRepository;
import uz.kundalik.site.repository.VerificationTokenRepository;
import uz.kundalik.site.service.generate.GenerateUniqueService;
import uz.kundalik.site.service.mail.MailService;
import uz.kundalik.site.service.sms.SmsService;
import uz.kundalik.site.service.template.AuthService;
import uz.kundalik.site.service.template.UserService;
import uz.kundalik.site.util.ContactUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final SmsService smsService;
    private final ObjectMapper objectMapper;
    private final GenerateUniqueService generateUniqueService;

    @Value("${application.security.verification.max-attempts:3}")
    Integer MAX_ATTEMPTS;
    @Value("${application.security.verification.lockout-duration-minutes:30}")
    Integer LOCKOUT_DURATION_MINUTES;


    /**
     * Registers a new user with the {@code USER} role.
     * The user is created in an inactive state (enabled=false) pending email verification.
     * An email with a verification link is sent to the user's provided email address.
     *
     * @param requestDTO The DTO containing registration details (email, password, profile info).
     * @return A DTO with the new user's ID and a message indicating that verification is required.
     * @throws EmailAlreadyExistsException if the email is already in use.
     */
    @Override
    public UserRegisterResponseDTO registerUser(UserRegisterRequestDTO requestDTO) {

        if (requestDTO.getEmail() != null) {
            if (userRepository.existsByEmail(requestDTO.getEmail())) {
                throw new EmailAlreadyExistsException("An account with this email already exists: " + requestDTO.getEmail());
            }
        }

        User newUser = new User();
        newUser.setEmail(requestDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        newUser.setRole(Role.USER);
        newUser.setEnabled(false);
//        newUser.setEmailVerified(false);
        newUser.setAccountNonLocked(true);

        UserProfile profile = new UserProfile();
        profile.setFirstName(requestDTO.getFirstName());
        profile.setLastName(requestDTO.getLastName());
        profile.setBirthDate(requestDTO.getBirthDate());
        profile.setGender(requestDTO.getGender());

        newUser.linkProfile(profile);

        User savedUser = userRepository.save(newUser);

        String verificationCode = generateUniqueService.generateUniqueSixDigitToken(TokenType.ACCOUNT_ACTIVATION);
        createAndSaveToken(savedUser, verificationCode, TokenType.ACCOUNT_ACTIVATION);
        mailService.sendVerificationEmail(savedUser, verificationCode);

        return new UserRegisterResponseDTO(
                savedUser.getId(),
                "Registration successful. Please check your email to verify your account.",
                VerificationMethod.EMAIL,
                savedUser.getEmail()
        );


    }

    /**
     * Verifies a 6-digit code sent to the user's contact information (email or phone).
     * <p>
     * This method is used during:
     * <ul>
     *   <li>User registration — to activate a newly created account.</li>
     *   <li>Account recovery flows — such as confirming phone/email changes (if extended later).</li>
     * </ul>
     * <p>
     * The method validates the code against the database and, if valid and not expired:
     * <ul>
     *   <li>Marks the user as {@code enabled=true} and {@code emailVerified=true} (for email verification).</li>
     *   <li>Deletes or invalidates the verification token to prevent reuse.</li>
     * </ul>
     *
     * @param requestDTO A DTO containing the user's contact (email or phone) and the 6-digit code.
     * @return A message response indicating whether the verification was successful or failed.
     * @throws InvalidTokenException   if the code is invalid or expired.
     * @throws EntityNotFoundException if the user associated with the contact cannot be found.
     */
    @Override
    public MessageResponseDTO verifyUserCode(VerifyCodeRequestDTO requestDTO) {
        String code = requestDTO.getCode();
        VerificationToken verificationToken = verificationTokenRepository.findByToken(code)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification code."));

        User user = verificationToken.getUser();

        if (user.getEmail().equals(requestDTO.getContact())) {
//            user.setEmailVerified(true);
            user.setEnabled(true);
            userRepository.save(user);

            verificationTokenRepository.delete(verificationToken);

            return new MessageResponseDTO("Account has been verified successfully.");
        }


        return new MessageResponseDTO("Invalid verification code.");
    }

    /**
     * Authenticates a user based on their credentials.
     * If authentication is successful, it generates and returns JWT access and refresh tokens.
     *
     * @param loginDTO The DTO containing the user's email and password.
     * @return A DTO containing the access and refresh tokens.
     * @throws AuthenticationException if credentials are invalid.
     */
    @Override
    public TokenDTO loginUser(LoginDTO loginDTO) {
        String emailOrPhoneNumber = loginDTO.getEmail();
        log.info("User '{}' attempting to log in", emailOrPhoneNumber);

        User user = (User) userService.loadUserByUsername(emailOrPhoneNumber);
//        User user = userRepository.findForLogin(emailOrPhoneNumber)
//                .orElseThrow(() -> new EntityNotFoundException("Username not found " + emailOrPhoneNumber));


        if (!user.isEnabled()) {
            throw new BadCredentialsException("Hisob faollashtirilmagan. Iltimos, emailingizni tasdiqlang.");
        }

        try {

            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Bad credentials provided");
            }

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            TokenDTO tokenDto = TokenDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)

                    .expiresIn(jwtProperties.getAccessTokenExpiration().toSeconds())
                    .authorities(user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet()))
                    .email(user.getUsername())
                    .build();

            log.info("User '{}' successfully authenticated and tokens generated.", emailOrPhoneNumber);

            return tokenDto;

        } catch (UsernameNotFoundException e) {

            log.warn("Failed login attempt for non-existent user '{}'", emailOrPhoneNumber);
            throw new BadCredentialsException("Bad credentials provided");
        }
    }

    /**
     * Refreshes an expired access token using a valid refresh token.
     *
     * @param requestDTO The DTO containing the refresh token.
     * @return A new DTO with a fresh access token and the original refresh token.
     * @throws InvalidTokenException if the refresh token is invalid or expired.
     */
    @Override
    public TokenDTO refreshToken(RefreshTokenRequestDTO requestDTO) {

        if (!jwtService.isRefreshTokenValid(requestDTO.getRefreshToken())) {
            throw new InvalidTokenException("Invalid refresh token.");
        }

        String username = jwtService.extractUsername(requestDTO.getRefreshToken());

        User user = (User) userService.loadUserByUsername(username);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        TokenDTO tokenDto = TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)

                .expiresIn(jwtProperties.getAccessTokenExpiration().toSeconds())
                .authorities(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .email(user.getUsername())
                .build();

        log.info("User '{}' successfully authenticated and tokens generated:", username);

        return tokenDto;
    }

    /**
     * Initiates the password reset process for a user.
     * It generates a password reset token and sends a reset link to the user's email.
     * The response is always generic to prevent user enumeration attacks.
     *
     * @param requestDTO The DTO containing the user's email.
     * @return A generic success message.
     */
    @Override
    @Transactional
    public MessageResponseDTO initiatePasswordReset(ForgotPasswordRequestDTO requestDTO) {
        String identifier = requestDTO.getEmail();

        Optional<User> userOptional = userRepository.findByEmailAndDeletedIsFalse(identifier);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            extracted(user);

            String tokenString = UUID.randomUUID().toString();
            VerificationToken passwordResetToken = new VerificationToken();
            passwordResetToken.setToken(tokenString);
            passwordResetToken.setUser(user);
            passwordResetToken.setTokenType(TokenType.PASSWORD_RESET);
            passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
            verificationTokenRepository.save(passwordResetToken);

            mailService.sendPasswordResetEmail(user, tokenString);
        } else {
            log.warn("Password reset initiated for a non-existent identifier: {}", identifier);
        }
        return new MessageResponseDTO("If an account with that identifier exists, password reset instructions have been sent.");
    }

    /**
     * Completes the password reset process by setting a new password for the user.
     *
     * @param token      The password reset token from the email link.
     * @param requestDTO The DTO containing the new password and its confirmation.
     * @return A success message upon completion.
     * @throws InvalidTokenException     if the token is invalid or expired.
     * @throws PasswordMismatchException if the new password and confirmation do not match.
     */
    @Override
    @Transactional
    public MessageResponseDTO completePasswordReset(String token, ResetPasswordRequestDTO requestDTO) {
        VerificationToken resetToken = verificationTokenRepository
                .findByTokenAndTokenType(token, TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired password reset token."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(resetToken);
            throw new InvalidTokenException("Password reset token has expired.");
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);

        verificationTokenRepository.delete(resetToken);

        return new MessageResponseDTO("Password has been successfully reset. You can now log in with your new password.");
    }

    @Override
    @Transactional
    public String processEmailChangeConfirmation(String token, String redirectUrl) {
        log.info("Processing email change confirmation with token: {}", token);

        VerificationToken verificationToken = verificationTokenRepository
                .findByTokenAndTokenType(token, TokenType.EMAIL_CHANGE)
                .orElseThrow(() -> {
                    log.warn("Attempt to confirm email change with an invalid token: {}", token);
                    return new InvalidTokenException("Invalid or unrecognized email change token.");
                });

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            log.warn("Attempt to use an expired email change token for user: {}", verificationToken.getUser().getId());
            throw new InvalidTokenException("Email change token has expired.");
        }

        User user = verificationToken.getUser();
        String metadataJson = verificationToken.getMetadata();

        if (metadataJson == null || metadataJson.isBlank()) {
            log.error("Critical error: Email change token {} is missing metadata.", token);
            throw new IllegalStateException("Email change token is missing required metadata.");
        }

        String newEmail;
        try {
            Map<String, String> metadata = objectMapper.readValue(metadataJson, new TypeReference<>() {
            });
            newEmail = metadata.get("newEmail");
            if (newEmail == null || newEmail.isBlank()) {
                throw new IllegalStateException("Metadata for email change token is malformed.");
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse metadata JSON for token: {}. Metadata: '{}'", token, metadataJson, e);
            throw new RuntimeException("Internal server error during token processing.");
        }

        log.info("Updating email for user {} from '{}' to '{}'", user.getId(), user.getEmail(), newEmail);
        user.setEmail(newEmail);
//        user.setEmailVerified(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
        log.info("Successfully confirmed email change for user {} and deleted the token.", user.getId());

        return redirectUrl;
    }


    @Override
    @Transactional
    public MessageResponseDTO resendInitialVerification(ResendVerificationRequestDTO requestDTO) {
        String identifier = requestDTO.getEmail();
        User user = userRepository.findByEmailAndEnabledFalseAndDeletedFalse(identifier)
                .orElseThrow(() -> new BadRequestException("No pending verification found for this identifier, or the account is already active."));

        extracted(user);

        verificationTokenRepository.deleteByUserAndTokenType(user, TokenType.ACCOUNT_ACTIVATION);

        String newVerificationCode = generateUniqueService.generateUniqueSixDigitToken(TokenType.ACCOUNT_ACTIVATION);
        createAndSaveToken(user, newVerificationCode, TokenType.ACCOUNT_ACTIVATION); // Yordamchi metod

        mailService.sendVerificationEmail(user, newVerificationCode);

        return new MessageResponseDTO("A new verification message has been sent. Please check your inbox or SMS.");
    }

    private void extracted(User user) {
        LocalDateTime now = LocalDateTime.now();

        if (user.getVerificationCodeRequestCount() >= MAX_ATTEMPTS) {
            LocalDateTime lockoutEndTime = user.getLastVerificationCodeRequestAt().plusMinutes(LOCKOUT_DURATION_MINUTES);

            if (lockoutEndTime.isAfter(now)) {
                long minutesRemaining = ChronoUnit.MINUTES.between(now, lockoutEndTime);
                throw new TooManyRequestsException("You have exceeded the maximum number of requests. Please try again in " + (minutesRemaining + 1) + " minutes.");
            } else {
                user.setVerificationCodeRequestCount(0);
            }
        }

        user.setVerificationCodeRequestCount(user.getVerificationCodeRequestCount() + 1);
        user.setLastVerificationCodeRequestAt(now);
        userRepository.save(user);
    }

    private void createAndSaveToken(User user, String token, TokenType type) {
        VerificationToken newToken = new VerificationToken();
        newToken.setToken(token);
        newToken.setUser(user);
        newToken.setTokenType(type);
        newToken.setExpiryDate(LocalDateTime.now().plusMinutes(10)); // Masalan, 10 daqiqalik yaroqlilik muddati
        verificationTokenRepository.save(newToken);
    }
}
