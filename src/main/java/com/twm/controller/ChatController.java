package com.twm.controller;

import com.twm.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @RequestMapping(value = "/chat", method = RequestMethod.POST)
    public ResponseEntity<?> chat(@RequestParam("msg") String prompt) {
        Map<String, Object> response = chatService.chat(prompt);
        return ResponseEntity.ok(response);
    }

}
