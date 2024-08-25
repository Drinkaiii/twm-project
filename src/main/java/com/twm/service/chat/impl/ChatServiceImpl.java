package com.twm.service.chat.impl;

import com.twm.dto.ButtonDto;
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

        log.info("sessionId : " + sessionId);

        messages.add(new SystemMessage("這些是你們的對話紀錄 : " + sessionHistory));

        log.info("sessionHistory : " + sessionHistory);

        messages.add(new SystemMessage("你是一位台灣大哥大的 GeForce Now 的客服人員，" +
                "最新活動與詳細資料都在此網站：https://www.nvidia.com/zh-tw/geforce-now/，" +
                "常見問答都在此網站：https://gfn.taiwanmobile.com/portal/qa" +
                "請有耐心地回覆客戶的詢問並給予有效的資訊" +
                "只能回答以上網站有出現的資訊" +
                "遇到無關的問題你也不能暴露自己身分，你要假設自己是真人回答" +
                "如果找不到資料就請他去這裡(https://gfn.taiwanmobile.com/portal/qa)"));
        messages.add(new UserMessage(question));

        ChatResponse response = openAiChatModel
                .call(new Prompt(
                        messages,
                        OpenAiChatOptions.builder()
                                .withTemperature(0f)
                                .build()
                ));

        String responseContent = response.getResult().getOutput().getContent();
        chatRepository.saveSession(userId, sessionId, question, responseContent);

        Map<String, Object> result = new HashMap<>();
        result.put("data", responseContent);
        result.put("sessionId", sessionId);

        return result;
    }

    @Override
    public List<ButtonDto> getAllButtons() {
        return chatRepository.findAllButtons();
    }

}
