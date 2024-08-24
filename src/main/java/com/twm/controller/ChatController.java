package com.twm.controller;

import com.twm.service.chat.ChatService;
import com.twm.service.chat.impl.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

}
