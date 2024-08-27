package com.twm.service.chat.impl;

import com.twm.dto.ButtonDto;
import com.twm.dto.ReturnQuestionDto;
import com.twm.dto.TypesDto;
import com.twm.dto.error.ErrorResponseDto;
import com.twm.repository.chat.ChatRepository;
import com.twm.service.chat.ChatService;
import com.twm.util.JwtUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Resource
    private OpenAiChatModel openAiChatModel;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> chat(Long userId, String sessionId, String question, String token) {

        if(question.length() >= 100) {
            throw new RuntimeException("Failed to save session");
        }

        if(!jwtUtil.isTokenValid(token)) {
            throw new RuntimeException("Invalid access token");
        }

        List<Message> messages = new ArrayList<>();
        List<String> sessionHistory = new ArrayList<>();

        try {
            if(sessionId == null || sessionId.isEmpty()){
                sessionId = UUID.randomUUID().toString();
            }else {
                //namedParameterJdbcTemplate.query 執行查詢時，如果沒有匹配的記錄，它會返回一個空的 List<String>
                if(Objects.equals(chatRepository.getSessionHistory(sessionId), new ArrayList<>())) {
                    throw new RuntimeException("Failed to load history");
                }else {
                    sessionHistory = chatRepository.getSessionHistory(sessionId);
                }
            }
        }catch (RuntimeException e) {
            throw new RuntimeException("Failed to load history", e);
        }

        messages.add(new SystemMessage("這些是你們的對話紀錄 : " + sessionHistory));

        try {
            messages.add(new SystemMessage("這是你的人設 : " + chatRepository.getPersonality()));
            messages.add(new SystemMessage("常見問答都在此 : " + chatRepository.getFAQ()));
        }catch (RuntimeException e) {
            throw new RuntimeException("Failed to load agent", e);
        }


        messages.add(new UserMessage(question));

        ChatResponse response = openAiChatModel
                .call(new Prompt(
                        messages,
                        OpenAiChatOptions.builder()
                                .withTemperature(0f)
                                .build()
                ));

        String responseContent = response.getResult().getOutput().getContent();
        try {
            chatRepository.saveSession(userId, sessionId, question, responseContent);
        }catch (RuntimeException e){
            throw new RuntimeException("Failed to save session", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", responseContent);
        result.put("sessionId", sessionId);

        return result;
    }

    @Override
    public List<TypesDto> getAllTypeButtons() {
        return chatRepository.findAllTypeButtons();
    }

    @Override
    public List<ReturnQuestionDto> getButtonsByType(Long typeId) {
        return chatRepository.findButtonsByType(typeId);
    }

    @Override
    public String getAnswerByQuestion(Long buttonId){
        return chatRepository.findAnswerByQuestion(buttonId);
    }

}
