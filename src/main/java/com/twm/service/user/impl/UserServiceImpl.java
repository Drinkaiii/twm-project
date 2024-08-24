package com.twm.service.user.impl;

import com.twm.repository.user.UserRepository;
import com.twm.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


}
