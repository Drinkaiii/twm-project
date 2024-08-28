package com.twm.controller;

import com.twm.dto.ButtonDto;
import com.twm.dto.CreateButtonDto;
import com.twm.dto.error.ErrorResponseDto;
import com.twm.exception.custom.MissFieldException;
import com.twm.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/admin/chat")
@Slf4j
public class AdminController {

    private final ChatService chatService;

    @PostMapping("/create")
    public ResponseEntity<?> chatCreate(@RequestBody CreateButtonDto createButtonDto) {

        try {
            chatService.saveButton(createButtonDto);
//            Map<String, Object> response = chatService.saveButton(createButtonDto);

            return ResponseEntity.ok().build();
        }catch (MissFieldException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (RuntimeException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/review")
    public ResponseEntity<?> chatReview(@RequestParam(value = "id", defaultValue = "0") Integer id) {

        try {
            log.info("id : " + id);

            Map<String, Object> response = chatService.getButton(id);

            return ResponseEntity.ok(response);
        }catch (MissFieldException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (RuntimeException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/update")
    public ResponseEntity<?> chatUpdate(@RequestBody CreateButtonDto createButtonDto) {

        try {
            CreateButtonDto response = chatService.updateButton(createButtonDto);

            return ResponseEntity.ok(response);
        }catch (MissFieldException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (RuntimeException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> chatDelete(@RequestBody CreateButtonDto createButtonDto) {

        try {
            boolean result = chatService.deleteButton(createButtonDto.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("result", result);

            if(result == true) {
                return ResponseEntity.ok(response);
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        }catch (RuntimeException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

}
