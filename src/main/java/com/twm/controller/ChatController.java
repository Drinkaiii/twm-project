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
public class ChatController {

    private final ChatService chatService;

    @RequestMapping(value = "/chat", method = RequestMethod.POST)
    public ResponseEntity<?> chat(@RequestParam("msg") String prompt) {
        Map<String, Object> response = chatService.chat(prompt);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buttons")
    public ResponseEntity<List<ButtonDto>> getButtons() {
        List<ButtonDto> buttons = chatService.getAllButtons();
        log.info("buttons size: {}", buttons.size());
        return ResponseEntity.ok(buttons);
    }
}
