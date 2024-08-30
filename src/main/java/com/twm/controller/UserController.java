package com.twm.controller;

import com.twm.dto.UserDto;
import com.twm.dto.error.ErrorResponseDto;
import com.twm.dto.ResetPasswordDto;
import com.twm.exception.custom.*;
import com.twm.service.user.UserService;

import jakarta.mail.AuthenticationFailedException;
import jakarta.servlet.http.HttpSession;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            ErrorResponseDto<List<String>> errorResponse = ErrorResponseDto.error(errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (userService.validateCaptcha(userDto.getCaptcha(), session)) {
            return ResponseEntity.ok(userService.signUp(userDto));
        } else {
            ErrorResponseDto<String> errorResponse = ErrorResponseDto.error("Authentication failed");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, Object> signInRequest, HttpSession session) {
        return ResponseEntity.ok(userService.signIn(signInRequest,session));
    }

    @GetMapping("/email/reset-password")
    public ResponseEntity<?> sendResetPasswordEmail(String email, String captcha, HttpSession session) {
        if (!userService.validateCaptcha(captcha, session))
            return new ResponseEntity<>(ErrorResponseDto.error("captcha is wrong."), HttpStatus.BAD_REQUEST);
        userService.sendResetPasswordEmail(email);
        return ResponseEntity.ok(Map.of("result", "The email has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto, HttpSession session) {
        if (!userService.validateCaptcha(resetPasswordDto.getCaptcha(), session))
            return new ResponseEntity<>(ErrorResponseDto.error("captcha is wrong."), HttpStatus.BAD_REQUEST);
        if (userService.resetPassword(resetPasswordDto))
            return ResponseEntity.ok(Map.of("result", "The password has been updated."));
        else
            return new ResponseEntity(ErrorResponseDto.error("Something went woring. The password has not been updated."), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/solve-jwt")
    public ResponseEntity<?> solveJwt(@RequestParam("token") String token){
        Map<String, Object> claims = userService.solveJwt(token);
        if (claims != null)
            return ResponseEntity.ok(claims);
        else
            return new ResponseEntity(ErrorResponseDto.error("Invalid JWT"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/update-authTime")
    public ResponseEntity<?> updateAuthTime(@RequestParam String userId) {
        if (userService.updateAuthTime(userId)){
            return ResponseEntity.ok("The auth time has been updated");
        }else{
            throw new RuntimeException("Failed to update auth time");
        }
    }

    @ExceptionHandler(InvalidEmailFormatException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidEmailFormatException(InvalidEmailFormatException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProviderException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidEmailOrPasswordException(InvalidProviderException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidEmailOrPasswordException(LoginFailedException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicatedEmailExcetion.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidEmailOrPasswordException(DuplicatedEmailExcetion ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(VerificationFailedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidEmailOrPasswordException(VerificationFailedException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
