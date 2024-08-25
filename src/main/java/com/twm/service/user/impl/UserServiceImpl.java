package com.twm.service.user.impl;

import com.twm.dto.UserDto;
import com.twm.repository.user.UserRepository;
import com.twm.service.user.UserService;
import com.twm.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public Map<String, Object> signUp(UserDto userDto){
            if(userRepository.getNativeUserByEmailAndProvider(userDto.getEmail()) != null){
                throw new RuntimeException("Email already exists");
            }
            int userId = userRepository.createNativeUser(userDto);
            return generateAuthResponse(userRepository.getUserById(userId));
    }

    private Map<String, Object> generateAuthResponse(UserDto userDto){
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("id", userDto.getId());
        userInfo.put("provider",userDto.getProvider());
        userInfo.put("email",userDto.getEmail());
        String jwtToken = jwtUtil.getToken(userInfo);
        int expiresIn = jwtUtil.getExpiration();
        response.put("accessToken", jwtToken);
        response.put("accessExpired", expiresIn);
        response.put("user",userInfo);
        return response;
    }
}
