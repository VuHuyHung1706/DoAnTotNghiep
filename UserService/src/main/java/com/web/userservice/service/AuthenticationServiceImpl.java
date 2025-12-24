package com.web.userservice.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.web.userservice.dto.response.AuthenticationResponse;
import com.web.userservice.dto.response.ExchangeTokenResponse;
import com.web.userservice.dto.response.GoogleUserResponse;
import com.web.userservice.dto.response.IntrospectResponse;
import com.web.userservice.dto.response.VerifyUsernameResponse;
import com.web.userservice.dto.request.*;
import com.web.userservice.entity.Account;
import com.web.userservice.entity.Customer;
import com.web.userservice.entity.InvalidatedToken;
import com.web.userservice.enums.Position;
import com.web.userservice.exception.AppException;
import com.web.userservice.exception.ErrorCode;
import com.web.userservice.repository.AccountRepository;
import com.web.userservice.repository.CustomerRepository;
import com.web.userservice.repository.InvalidatedTokenRepository;
import com.web.userservice.repository.ManagerRepository;
import com.web.userservice.repository.google.GoogleIdentityClient;
import com.web.userservice.repository.google.GoogleUserClient;
import com.web.userservice.service.mail.MailService;
import jakarta.transaction.Transactional;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${outbound.identity.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Autowired
    private GoogleIdentityClient googleIdentityClient;

    @Autowired
    private GoogleUserClient googleUserClient;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Account account = accountRepository.findByUsername(request.getUsername()).orElseThrow(()
                -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!account.getStatus()) {
            throw new AppException(ErrorCode.USER_NOT_ACTIVE);
        }

        Boolean authenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String token = generateJwtToken(account);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public AuthenticationResponse googleLogin(String code) {
        ExchangeTokenResponse response = googleIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        GoogleUserResponse userInfo = googleUserClient.getUserInfo("json", response.getAccessToken());

        if (!accountRepository.existsByUsername(userInfo.getEmail())) {
            Account googleAccount = Account.builder()
                    .username(userInfo.getEmail())
                    .password("")
                    .status(true)
                    .build();

            googleAccount = accountRepository.save(googleAccount);

            Customer customer = Customer.builder()
                    .firstName(userInfo.getGivenName())
                    .lastName(userInfo.getFamilyName())
                    .email(userInfo.getEmail())
                    .account(googleAccount)
                    .build();

            customerRepository.save(customer);
        }

        Account account = accountRepository.findByUsername(userInfo.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String token = generateJwtToken(account);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        }
        catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();

    }

    @Override
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try{
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        }
        catch (AppException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    @Override
    public AuthenticationResponse refresh(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user =
                accountRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateJwtToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    @Override
    public void forgotPassword(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        mailService.sendOtpForgotPassword(email);
    }

    @Override
    public String verifyOtpAndGenerateResetToken(String email, String otp) {
        Boolean isValid = mailService.verifyOtpForgotPassword(email, otp);
        if (!isValid) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        String resetToken = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set("reset_token:" + resetToken, email, 15, TimeUnit.MINUTES);

        mailService.sendResetPasswordLink(email, resetToken);

        return resetToken;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        String email = redisTemplate.opsForValue().get("reset_token:" + token);
        if (email == null) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String verified = redisTemplate.opsForValue().get("forgot_password_verified:" + email);
        if (verified == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Account account = customer.getAccount();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        redisTemplate.delete("reset_token:" + token);
        redisTemplate.delete("forgot_password_verified:" + email);
    }

    @Override
    public VerifyUsernameResponse verifyUsername(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String email = customer.getEmail();
        String maskedEmail = maskEmail(email);

        return VerifyUsernameResponse.builder()
                .email(email)
                .maskedEmail(maskedEmail)
                .build();
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            return email.charAt(0) + "***@" + domain;
        }

        int visibleChars = Math.min(2, localPart.length() / 3);
        String visible = localPart.substring(0, visibleChars);
        return visible + "***@" + domain;
    }

    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.DAYS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    public String generateJwtToken(Account account) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(account.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.DAYS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(account))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        }
        catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public String buildScope(Account account) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        var manager = managerRepository.findByAccountUsername(account.getUsername());
        if (manager.isPresent()) {
            Position position = manager.get().getPosition();
            if (position == Position.ADMIN) {
                stringJoiner.add("ROLE_ADMIN");
            } else if (position == Position.MANAGER) {
                stringJoiner.add("ROLE_MANAGER");
            } else if (position == Position.STAFF) {
                stringJoiner.add("ROLE_STAFF");
            } else {
                stringJoiner.add("ROLE_STAFF");
            }
        } else {
            stringJoiner.add("ROLE_CUSTOMER");
        }

        return stringJoiner.toString();
    }
}
