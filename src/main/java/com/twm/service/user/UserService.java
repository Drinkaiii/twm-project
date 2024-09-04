package com.twm.service.user;

import com.twm.dto.ResetPasswordDto;
import com.twm.dto.UserDto;
import com.twm.dto.supportDto;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

public interface UserService {
    Map<String, Object> signUp(UserDto userDto);

    Map<String, Object> signIn(Map<String, Object> signInRequest, HttpSession session);

    void sendResetPasswordEmail(String email);

    Boolean resetPassword(ResetPasswordDto resetPasswordDto);

    boolean validateCaptcha(String captchaId, String captcha);

    boolean validateEmail(String email);

    Boolean updateAuthTime(String userId);

    Map solveJwt(String token);

    Boolean saveSupportRequestRecord(supportDto supportDto);

}
