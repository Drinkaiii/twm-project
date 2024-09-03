package com.twm.service.chat.impl;

import com.twm.dto.PersonalityDto;
import com.twm.dto.ReturnCategoryDto;
import com.twm.dto.ReturnQuestionDto;
import com.twm.dto.TypesDto;
import com.twm.repository.admin.PersonalityRepository;
import com.twm.repository.chat.ChatRepository;
import com.twm.service.chat.ChatService;
import com.twm.util.JwtUtil;
import com.twm.util.RedisUtil;
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

    private final PersonalityRepository personalityRepository;

    @Resource
    private OpenAiChatModel openAiChatModel;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> chat(Long userId, String sessionId, String question) {

        if (question.length() >= 100) {
            throw new RuntimeException("Failed to save session");
        }

        List<Message> messages = new ArrayList<>();
        List<String> sessionHistory = new ArrayList<>();

        try {
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            } else {
                //namedParameterJdbcTemplate.query 執行查詢時，如果沒有匹配的記錄，它會返回一個空的 List<String>
                if (Objects.equals(chatRepository.getSessionHistory(sessionId), new ArrayList<>())) {
                    throw new RuntimeException("Failed to load history");
                } else {
                    sessionHistory = chatRepository.getSessionHistory(sessionId);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to load history", e);
        }

        messages.add(new SystemMessage("這些是你們的對話紀錄 : " + sessionHistory));

        try {
            String CACHE_KEY = "personality";
            boolean isCached = redisUtil.isCacheExist(CACHE_KEY);
            List<PersonalityDto> personalityDtos;

            if (isCached) {
                log.info("Data retrieved from Redis cache.");
                personalityDtos = redisUtil.getListDataFromCache(CACHE_KEY);
            } else {
                log.info("Data retrieved from the database.");
                personalityDtos = personalityRepository.getPersonality(0); //get sql data
                redisUtil.setJsonDataToCache(CACHE_KEY, personalityDtos);
            }
            messages.add(new SystemMessage("這是你的人設 : " + personalityDtos));
            messages.add(new SystemMessage("常見問答都在此 : " + chatRepository.getFAQ()));
        } catch (RuntimeException e) {
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
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to save session", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", responseContent);
        result.put("sessionId", sessionId);

        return result;
    }

    @Override
    public List<TypesDto> getAllTypeButtons() {
        String CACHE_KEY = "type";
        List<TypesDto> types;
        boolean isCached = redisUtil.isCacheExist(CACHE_KEY);

        if (isCached) {
            log.info("Data retrieved from Redis cache.");
            types = redisUtil.getListDataFromCache(CACHE_KEY);
        } else {
            log.info("Data retrieved from the database.");
            types = chatRepository.findAllTypeButtons(); //get sql data
            redisUtil.setJsonDataToCache(CACHE_KEY, types);
        }
        return types;
    }

    @Override
    public List<ReturnQuestionDto> getButtonsByType(Long typeId) {
        String CACHE_KEY = "button";
        List<ReturnQuestionDto> buttons;
        boolean isCached = redisUtil.isCacheExist(CACHE_KEY);

        if (isCached) {
            log.info("Data retrieved from Redis cache.");
            buttons = redisUtil.getListDataFromCache(CACHE_KEY);
        } else {
            log.info("Data retrieved from the database.");
            buttons = chatRepository.findButtonsByType(typeId); //get sql data
            redisUtil.setJsonDataToCache(CACHE_KEY, buttons);
        }

        return buttons;
    }

    @Override
    public String getAnswerByQuestion(Long buttonId) {
        String CACHE_KEY = "answer_" + buttonId;
        String answer;
        boolean isCached = redisUtil.isCacheExist(CACHE_KEY);

        if (isCached) {
            log.info("Data retrieved from Redis cache.");
            answer = redisUtil.getDataFromCache(CACHE_KEY);
        } else {
            log.info("Data retrieved from the database.");
            answer = chatRepository.findAnswerByQuestion(buttonId);
            redisUtil.setDataToCache(CACHE_KEY, answer);
        }

        return answer;
    }

    public List<ReturnCategoryDto> getAllCategoryButtons() {
        String CACHE_KEY = "category";
        List<ReturnCategoryDto> categories;
        boolean isCached = redisUtil.isCacheExist(CACHE_KEY);

        if (isCached) {
            log.info("Data retrieved from Redis cache.");
            categories = redisUtil.getListDataFromCache(CACHE_KEY);
        } else {
            log.info("Data retrieved from the database.");
            categories = chatRepository.findAllCategoryButtons(); //get sql data
            redisUtil.setJsonDataToCache(CACHE_KEY, categories);
        }

        return categories;
    }

    ;

    @Override
    public String getUrlByCategory(Long categoryId) {
        String CACHE_KEY = "url_" + categoryId;
        String url;
        boolean isCached = redisUtil.isCacheExist(CACHE_KEY);

        if (isCached) {
            log.info("Data retrieved from Redis cache.");
            url = redisUtil.getDataFromCache(CACHE_KEY);
        } else {
            log.info("Data retrieved from the database.");
            url = chatRepository.findUrlByCategory(categoryId);
            redisUtil.setDataToCache(CACHE_KEY, url);
        }

        return url;
    }

}
