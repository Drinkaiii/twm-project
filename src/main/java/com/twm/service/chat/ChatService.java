package com.twm.service.chat;

import com.twm.dto.*;

import java.util.List;
import java.util.Map;

public interface ChatService {

    List<TypesDto> getAllTypeButtons();
    List<ReturnQuestionDto> getButtonsByType(Long typeId);
    String getAnswerByQuestion(Long buttonId);
    Map<String, Object> chat(Long userId, String sessionId, String question);
    List<ReturnCategoryDto> getAllCategoryButtons();
    String getUrlByCategory(Long categoryId);
}
