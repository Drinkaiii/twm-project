package com.twm.repository.chat;

import com.twm.dto.ButtonDto;
import java.util.List;

public interface ChatRepository {

    List<String> getSessionHistory(String sessionId);

    void saveSession(Long userId, String sessionId, String question, String responseContent);

    List<ButtonDto> findAllButtons();

}
