package com.twm.service.user.impl;

import com.twm.dto.UserDto;
import com.twm.dto.ResetPasswordDto;
import com.twm.dto.supportDto;
import com.twm.exception.custom.*;
import com.twm.repository.user.UserRepository;
import com.twm.service.user.UserService;
import com.twm.util.EmailUtil;
import com.twm.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
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
    @Value("${redirectUrl}")
    private String redirectUrl;
    @Value("${twmToken}")
    private String twmToken;
    @Value("${twmBaseUrl}")
    private String twmBaseUrl;

    @Override
    public Map<String, Object> signUp(UserDto userDto) {
        if (userRepository.getNativeUserByEmailAndProvider(userDto.getEmail()) != null) {
            throw new DuplicatedEmailExcetion("Email already exists");
        }
        int userId = userRepository.createNativeUser(userDto);
        return generateAuthResponse(userRepository.getUserById(userId));
    }

    @Override
    public Map<String, Object> signIn(Map<String, Object> signInRequest, HttpSession session) {
        String provider = (String) signInRequest.get("provider");
        switch (provider.toLowerCase()) {
            case "native":
                if (!validateCaptcha((String) signInRequest.get("captcha"), session)) {
                    throw new VerificationFailedException("Authentication failed");
                }
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
                String accessToken = getAccessToken(signInRequest.get("accessToken").toString());
                Map<String, Object> userProfileMap = getTwmUserProfile(accessToken);
                UserDto twmUser = userRepository.getTwmUserByEmailAndProvider(userProfileMap.get("email").toString());
                if (twmUser == null) {
                    int userId = userRepository.createTwmUser(userProfileMap.get("email").toString());
                    return generateAuthResponse(userRepository.getUserById(userId));
                }else{
                    return generateAuthResponse(userRepository.getUserById(twmUser.getId().intValue()));
                }
            default:
                throw new InvalidProviderException("Invalid provider");
        }
    }

    @Override
    public Boolean updateAuthTime(String userId) {
        return userRepository.updateAuthTimeByUserId(userId, getCurrentTime()) > 0;
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

    @Override
    public Boolean saveSupportRequestRecord(supportDto supportDto){
        Integer affectedRows = userRepository.saveSupportRecords(supportDto.getName(),supportDto.getEmail(),supportDto.getDescription(),getCurrentTime());
        return affectedRows != null && affectedRows != 0;
    }

    private Map<String, Object> generateAuthResponse(UserDto userDto) {
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("id", userDto.getId());
        userInfo.put("provider", userDto.getProvider());
        userInfo.put("email", userDto.getEmail());
        userInfo.put("authTime", userDto.getAuthTime());
        userInfo.put("role", userDto.getRole());
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

    private String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    @Nullable
    @Override
    public Map solveJwt(String token) {
        return jwtUtil.isTokenValid(token) ? jwtUtil.getClaims(token) : null;
    }

    private String getAccessToken(String code) {

        RestTemplate restTemplate = new RestTemplate();

        // set header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + twmToken);

        // set request parameters
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("redirect_uri", redirectUrl);

        // create request entity
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // send a request and receive a response
        ResponseEntity<Map> response = restTemplate.postForEntity(twmBaseUrl, request, Map.class);
        return response.getBody().get("access_token").toString();
    }


    private Map<String, Object> getTwmUserProfile(String accessToken) {

        RestTemplate restTemplate = new RestTemplate();

        // set header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        // create an empty request entity with headers only
        HttpEntity<String> request = new HttpEntity<>(headers);

        // send a POST request and receive a response
        ResponseEntity<Map> response = restTemplate.postForEntity("https://stage.oauth.taiwanmobile.com/MemberOAuth/getUserProfile", request, Map.class);

        return response.getBody();
    }

}



