package com.twm.service.chat.impl;

import com.twm.dto.ButtonDto;
import com.twm.dto.TypesDto;
import com.twm.repository.chat.ChatRepository;
import com.twm.service.chat.ChatService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Resource
    private OpenAiChatModel openAiChatModel;

    @Override
    public Map<String, Object> chat(String prompt) {

        List<Message> messages = List.of(
                new SystemMessage("你是一位台灣大哥大的 GeForce Now 的客服人員，" +
                        "最新活動與詳細資料都在此網站：https://www.nvidia.com/zh-tw/geforce-now/，" +
                        "常見問答都在此網站：https://gfn.taiwanmobile.com/portal/qa" +
                        "請有耐心地回覆客戶的詢問並給予有效的資訊" +
                        "只能回答以上網站有出現的資訊" +
                        "遇到無關的問題你也不能暴露自己身分，你要假設自己是真人回答" +
                        "如果找不到資料就請他去這裡(https://gfn.taiwanmobile.com/portal/qa)"),
                new UserMessage(prompt)
        );
        ChatResponse response = openAiChatModel
                .call(new Prompt(
                        messages,
                        OpenAiChatOptions.builder()
                                .withTemperature(0f)
                                .build()
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("response", response);

        return result;
    }

    @Override
    public List<TypesDto> getAllTypeButtons() {
        return chatRepository.findAllTypeButtons();
    }

    @Override
    public List<ButtonDto> getButtonsByType(Long typeId) {
        return chatRepository.findButtonsByType(typeId);
    }

    @Override
    public String getAnswerByQuestion(Long buttonId){
        return chatRepository.findAnswerByQuestion(buttonId);
    }

}
