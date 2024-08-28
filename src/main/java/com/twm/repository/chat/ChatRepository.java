package com.twm.repository.chat;

import com.twm.dto.*;
import java.util.List;

public interface ChatRepository {

    List<ReturnQuestionDto> findButtonsByType(Long typeId);
    String findAnswerByQuestion(Long buttonId);
    void saveButton(CreateButtonDto createButtonDto);
    List<CreateButtonDto> getButton(Integer id);
    CreateButtonDto updateButton(CreateButtonDto createButtonDto);
    List<String> getSessionHistory(String sessionId);
    void saveSession(Long userId, String sessionId, String question, String responseContent);
    List<String> getFAQ();
    List<String> getPersonality();
    List<TypesDto> findAllTypeButtons();
    List<ReturnCategoryDto> findAllCategoryButtons();
    String findUrlByCategory(Long categoryId);

}
