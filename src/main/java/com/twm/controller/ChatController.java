package com.twm.controller;

import com.twm.dto.ButtonDto;
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
    public ResponseEntity<?> chat(@RequestParam("msg") String message, @RequestParam String sessionId) {
        Map<String, Object> response = chatService.chat(1, sessionId, message);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/buttons")
    public ResponseEntity<List<ButtonDto>> getButtons() {
        List<ButtonDto> buttons = chatService.getAllButtons();
        return ResponseEntity.ok(buttons);
    }
}
