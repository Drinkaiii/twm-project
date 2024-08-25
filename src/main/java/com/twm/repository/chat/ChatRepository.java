package com.twm.repository.chat;

import java.util.List;

public interface ChatRepository {

    List<String> getSessionHistory(String sessionId);

    void saveSession(Integer userId, String sessionId, String message, String responseContent);

}
