package com.twm.service.chat;

import com.twm.dto.*;

import java.util.List;
import java.util.Map;

public interface ChatService {

    List<TypesDto> getAllTypeButtons();
    List<ReturnQuestionDto> getButtonsByType(Long typeId);
    String getAnswerByQuestion(Long buttonId);
    List<ReturnCategoryDto> getAllCategoryButtons();
    String getUrlByCategory(Long categoryId);
    Map<String, Object> chat(Long userId, String sessionId, String question);
//    void saveButton(CreateButtonDto createButtonDto);
//    Map<String, Object> getButton(Integer id);
//    CreateButtonDto updateButton(CreateButtonDto createButtonDto);
//    boolean deleteButton(Long id);
//    Map<String, Object> savePersonality(PersonalityDto personalityDto);
//    Map<String, Object> getPersonality(Integer id);
}
