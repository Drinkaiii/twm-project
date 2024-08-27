package com.twm.repository.chat;

import com.twm.dto.*;

import java.util.List;

public interface ChatRepository {

    List<ReturnQuestionDto> findButtonsByType(Long typeId);
    String findAnswerByQuestion(Long buttonId);
    List<String> getSessionHistory(String sessionId);
    void saveSession(Long userId, String sessionId, String question, String responseContent);
    List<String> getFAQ();
    List<String> getPersonality();
    List<TypesDto> findAllTypeButtons();
    List<ReturnCategoryDto> findAllCategoryButtons();
    String findUrlByCategory(Long categoryId);

}
