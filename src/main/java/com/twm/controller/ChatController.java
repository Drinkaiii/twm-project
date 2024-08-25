package com.twm.controller;

import com.twm.dto.ButtonDto;
import com.twm.dto.RecordDto;
import com.twm.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        Map<String, Object> response = chatService.chat(userId, sessionId, question);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/buttons")
    public ResponseEntity<List<ButtonDto>> getButtons() {
        List<ButtonDto> buttons = chatService.getAllButtons();
        return ResponseEntity.ok(buttons);
    }
}
