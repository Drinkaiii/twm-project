package com.twm.service.chat.impl;

import com.twm.dto.ButtonDto;
import com.twm.dto.ReturnQuestionDto;
import com.twm.dto.TypesDto;
import com.twm.repository.chat.ChatRepository;
import com.twm.service.chat.ChatService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Resource
    private OpenAiChatModel openAiChatModel;

    @Override
    public Map<String, Object> chat(Long userId, String sessionId, String question) {

        List<Message> messages = new ArrayList<>();
        List<String> sessionHistory = new ArrayList<>();

        if(sessionId == null || sessionId.isEmpty()){
            sessionId = UUID.randomUUID().toString();
        }else{
            sessionHistory = chatRepository.getSessionHistory(sessionId);
        }

        log.info("sessionHistory : " + sessionHistory);

        messages.add(new SystemMessage("這些是你們的對話紀錄 : " + sessionHistory));

        log.info("chatRepository.getPersonality() : " + chatRepository.getPersonality());

        messages.add(new SystemMessage("這是你的人設 : " + chatRepository.getPersonality()));

        messages.add(new SystemMessage("常見問答都在此 : " + chatRepository.getFAQ()));

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
