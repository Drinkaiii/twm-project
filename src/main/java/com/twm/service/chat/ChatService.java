package com.twm.service.chat;

import java.util.Map;

public interface ChatService {

    Map<String, Object> chat(Integer userId, String sessionId, String prompt);

}
