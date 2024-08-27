package com.twm.repository.chat;

import com.twm.dto.CreateButtonDto;
import com.twm.dto.ReturnQuestionDto;
import com.twm.dto.TypesDto;
import java.util.List;

public interface ChatRepository {

    List<TypesDto> findAllTypeButtons();
    List<ReturnQuestionDto> findButtonsByType(Long typeId);
    String findAnswerByQuestion(Long buttonId);
    Integer saveButton(CreateButtonDto createButtonDto);
    List<String> getSessionHistory(String sessionId);
    void saveSession(Long userId, String sessionId, String question, String responseContent);
    List<String> getFAQ();
    List<String> getPersonality();

}
