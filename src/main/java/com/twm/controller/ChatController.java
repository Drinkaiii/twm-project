package com.twm.controller;

import com.twm.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/agents")
    public ResponseEntity<?> continueChat(@RequestParam("msg") String message, @RequestParam String sessionId) {
        Map<String, Object> response = chatService.chat(1, sessionId, message);
        return ResponseEntity.ok(response);
    }

}
