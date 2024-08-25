package com.twm.repository.chat;

import com.twm.dto.ButtonDto;
import java.util.List;

public interface ChatRepository {

    List<String> getSessionHistory(String sessionId);

    void saveSession(Integer userId, String sessionId, String message, String responseContent);

    List<ButtonDto> findAllButtons();

}
