package com.twm.controller;

import com.twm.dto.error.ChatRequest;
import com.twm.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest chatRequest) {
        log.info(chatRequest.toString());
        Map<String, Object> response = chatService.chat(chatRequest.getUserMessage());
        log.info(response.toString());
        return ResponseEntity.ok(response);
    }

}
