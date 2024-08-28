package com.twm.controller;

import com.twm.dto.ButtonDto;
import com.twm.dto.CreateButtonDto;
import com.twm.dto.error.ErrorResponseDto;
import com.twm.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/admin/chat")
@Slf4j
public class AdminController {

    private final ChatService chatService;

    @PostMapping("/create")
    public ResponseEntity<?> chatCreate(@RequestBody CreateButtonDto createButtonDto,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        try {
            String token = authorization.split(" ")[1].trim();

            Map<String, Object> response = chatService.saveButton(createButtonDto, token);

            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/review")
    public ResponseEntity<?> chatReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                        @RequestParam(value = "id", defaultValue = "0") Integer id) {

        try {
            String token = authorization.split(" ")[1].trim();

            log.info("id : " + id);

            Map<String, Object> response = chatService.getButton(id, token);

            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

}
