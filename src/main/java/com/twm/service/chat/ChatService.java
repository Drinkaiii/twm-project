package com.twm.service.chat;

import com.twm.dto.ButtonDto;
import com.twm.dto.ReturnQuestionDto;
import com.twm.dto.TypesDto;

import java.util.List;
import java.util.Map;

public interface ChatService {

    List<TypesDto> getAllTypeButtons();
    List<ReturnQuestionDto> getButtonsByType(Long typeId);
    String getAnswerByQuestion(Long buttonId);
    Map<String, Object> chat(Long userId, String sessionId, String question);


}
