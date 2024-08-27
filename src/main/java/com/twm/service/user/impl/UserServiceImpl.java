package com.twm.service.user.impl;

import com.twm.dto.UserDto;
import com.twm.dto.ResetPasswordDto;
import com.twm.exception.custom.InvalidEmailFormatException;
import com.twm.exception.custom.InvalidProviderException;
import com.twm.exception.custom.LoginFailedException;
import com.twm.repository.user.UserRepository;
import com.twm.service.user.UserService;
import com.twm.util.EmailUtil;
import com.twm.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtil emailUtil;
    @Value("${domain}")
    private String domain;

    @Override
    public Map<String, Object> signUp(UserDto userDto) {
        if (userRepository.getNativeUserByEmailAndProvider(userDto.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        int userId = userRepository.createNativeUser(userDto);
        return generateAuthResponse(userRepository.getUserById(userId));
    }

    @Override
    public Map<String, Object> signIn(Map<String, Object> signInRequest) {
        String provider = (String) signInRequest.get("provider");
        switch (provider.toLowerCase()) {
            case "native":
                String email = (String) signInRequest.get("email");
                if (!validateEmail(email)) {
                    throw new InvalidEmailFormatException("Invalid email format");
                }
                UserDto nativeUser = userRepository.getNativeUserByEmailAndProvider(email);
                if (nativeUser == null || !passwordEncoder.matches((String) signInRequest.get("password"), nativeUser.getPassword())) {
                    throw new LoginFailedException("Invalid email or password");
                }
                return generateAuthResponse(userRepository.getUserById(nativeUser.getId().intValue()));
            case "twm":
            default:
                throw new InvalidProviderException("Invalid provider");
        }
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        Map<String, Object> map = Map.of("email", email);
        String token = jwtUtil.getToken(map);
        emailUtil.sendEmail(email, "【密碼變更驗證信件】台灣大哥大客戶服務中心", domain + "account_reset.html?token=" + token);
    }

    @Override
    public Boolean resetPassword(ResetPasswordDto resetPasswordDto) {
        Map<String, Object> claims = jwtUtil.getClaims(resetPasswordDto.getResetPasswordToken());
        if (jwtUtil.isTokenValid(resetPasswordDto.getResetPasswordToken())) {
            String email = (String) claims.get("email");
            UserDto userDto = userRepository.getNativeUserByEmailAndProvider(email);
            userDto.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
            userRepository.updatePasswordByEmail(userDto);
            return true;
        } else {
            return false;
        }
    }

    private Map<String, Object> generateAuthResponse(UserDto userDto) {
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("id", userDto.getId());
        userInfo.put("provider", userDto.getProvider());
        userInfo.put("email", userDto.getEmail());
        String jwtToken = jwtUtil.getToken(userInfo);
        int expiresIn = jwtUtil.getExpiration();
        response.put("accessToken", jwtToken);
        response.put("accessExpired", expiresIn);
        response.put("user", userInfo);
        return response;
    }

    private Boolean validateEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (email == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validateCaptcha(String captchaInput, HttpSession session) {
        String captchaSession = (String) session.getAttribute(KAPTCHA_SESSION_KEY);
        return captchaSession != null && captchaSession.equals(captchaInput);
    }
}
