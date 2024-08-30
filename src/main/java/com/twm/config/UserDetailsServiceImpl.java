package com.twm.config;

import com.twm.dto.UserDto;
import com.twm.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDto userDto = userRepository.getNativeUserByEmailAndProvider(username);
        if (userDto == null)
            throw new UsernameNotFoundException("User not found");

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userDto.getRole()));

        return org.springframework.security.core.userdetails.User.builder()
                .username(userDto.getEmail())
                .password(userDto.getPassword())
                .authorities(authorities)
                .build();
    }
}
