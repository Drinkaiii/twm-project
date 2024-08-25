package com.twm.controller;

import com.twm.dto.ButtonDto;
import com.twm.dto.RecordDto;
import com.twm.dto.error.ErrorResponseDto;
import com.twm.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/agents")
    public ResponseEntity<?> chat(@RequestBody RecordDto recordDto) {

        Long userId = 1L;
//      Long userId = recordDto.getUserId();
        String sessionId = recordDto.getSessionId();
        log.info("sessionId : " + sessionId);
        String question = recordDto.getQuestion();

        try {
            Map<String, Object> response = chatService.chat(userId, sessionId, question);

            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return new ResponseEntity<>(ErrorResponseDto.error("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDto.error("The agent is broken."), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/buttons")
    public ResponseEntity<List<ButtonDto>> getButtons() {
        List<ButtonDto> buttons = chatService.getAllButtons();
        return ResponseEntity.ok(buttons);
    }
}
