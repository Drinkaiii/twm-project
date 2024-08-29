package com.twm.repository.chat;

import com.twm.dto.*;
import java.util.List;

public interface ChatRepository {

    List<ReturnQuestionDto> findButtonsByType(Long typeId);
    String findAnswerByQuestion(Long buttonId);
    void saveButton(CreateButtonDto createButtonDto);
    List<CreateButtonDto> getButton(Integer id);
    CreateButtonDto updateButton(CreateButtonDto createButtonDto);
    boolean deleteButton(Long id);
    List<String> getSessionHistory(String sessionId);
    void saveSession(Long userId, String sessionId, String question, String responseContent);
    List<String> getFAQ();
    boolean savePersonality(PersonalityDto personalityDto);
    List<PersonalityDto> getPersonality(Integer id);
    List<TypesDto> findAllTypeButtons();
    List<ReturnCategoryDto> findAllCategoryButtons();
    String findUrlByCategory(Long categoryId);

}
