package com.twm.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;

@Data
@AllArgsConstructor
@RequestMapping
public class ChatRequest {
    private String userId;
    private String sessionId;
    private String userMessage;
}
