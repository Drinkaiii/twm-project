package com.twm.controller;

import com.twm.dto.UserDto;
import com.twm.dto.error.ErrorResponseDto;
import com.twm.service.user.UserService;
import com.twm.service.user.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            ErrorResponseDto<List<String>> errorResponse = ErrorResponseDto.error(errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        try {
            return ResponseEntity.ok(userService.signUp(userDto));
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(ErrorResponseDto.error("failed to signup"));
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error",e.getMessage()));
        }
         catch (Exception e){
            log.error(e.getMessage());
            throw e;
        }
    }


}
