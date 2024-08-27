package com.twm.service.user;

import com.twm.dto.UserDto;
import com.twm.dto.ResetPasswordDto;

import java.util.Map;

public interface UserService {
    Map<String, Object> signUp(UserDto userDto);
    Map<String, Object> signIn(Map<String, Object> signInRequest);
    void sendResetPasswordEmail(String email);
    Boolean resetPassword(ResetPasswordDto resetPasswordDto);
}
