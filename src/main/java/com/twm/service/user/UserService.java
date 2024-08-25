package com.twm.service.user;

import com.twm.dto.UserDto;

import java.util.Map;

public interface UserService {
    Map<String, Object> signUp(UserDto userDto);
    Map<String, Object> signIn(Map<String, Object> signInRequest);
}
