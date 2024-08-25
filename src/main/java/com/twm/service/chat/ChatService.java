package com.twm.service.chat;

import com.twm.dto.ButtonDto;

import java.util.List;
import java.util.Map;

public interface ChatService {

    Map<String, Object> chat(Long userId, String sessionId, String question);

    List<ButtonDto> getAllButtons();

}
