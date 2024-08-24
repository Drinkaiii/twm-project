package com.twm.service.chat.impl;

import com.twm.repository.chat.ChatRepository;
import com.twm.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

}
