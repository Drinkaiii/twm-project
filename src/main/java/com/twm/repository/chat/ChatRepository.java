package com.twm.repository.chat;

import com.twm.dto.ButtonDto;
import com.twm.dto.ReturnQuestionDto;
import com.twm.dto.TypesDto;
import java.util.List;

public interface ChatRepository {

    List<TypesDto> findAllTypeButtons();
    List<ReturnQuestionDto> findButtonsByType(Long typeId);
    String findAnswerByQuestion(Long buttonId);
    List<String> getSessionHistory(String sessionId);
    void saveSession(Long userId, String sessionId, String question, String responseContent);
    List<String> getFAQ();
    List<String> getPersonality();

}
