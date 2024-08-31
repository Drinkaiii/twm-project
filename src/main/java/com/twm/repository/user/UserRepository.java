package com.twm.repository.user;

import com.twm.dto.UserDto;

public interface UserRepository {
    Integer createNativeUser(UserDto userDto);
    UserDto getNativeUserByEmailAndProvider(String email);
    UserDto getUserById(int userId);
    UserDto updatePasswordByEmail(UserDto userDto);
    int updateAuthTimeByUserId(String userId, String authTime);
    UserDto getTwmUserByEmailAndProvider(String email);
    Integer createTwmUser (String email);
    Integer saveSupportRecords(String name,String email,String description,String requestTime);
}
