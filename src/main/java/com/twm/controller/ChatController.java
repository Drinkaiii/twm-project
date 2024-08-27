package com.twm.controller;

import com.twm.dto.ButtonDto;
import com.twm.dto.ReturnQuestionDto;
import com.twm.dto.TypesDto;
import com.twm.generic.ApiResponse;
import com.twm.dto.RecordDto;
import com.twm.dto.error.ErrorResponseDto;
import com.twm.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/agents")
    public ResponseEntity<?> chat(@RequestBody RecordDto recordDto,
                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        Long userId = 1L;
        String sessionId = recordDto.getSessionId();
        String question = recordDto.getQuestion();

        try {
            String token = authorization.split(" ")[1].trim();

            Map<String, Object> response = chatService.chat(userId, sessionId, question, token);

            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDto.error("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/routines")
    public ResponseEntity<ApiResponse<?>> getButtonsByParam(
            @RequestParam(value = "type", required = false) Long typeId,
            @RequestParam(value = "question", required = false) Long buttonId) {

        if (typeId != null) {
            List<ReturnQuestionDto> buttons = chatService.getButtonsByType(typeId);
            return ResponseEntity.ok(new ApiResponse<>(buttons));
        } else if (buttonId != null) {
            String answer = chatService.getAnswerByQuestion(buttonId);
            return ResponseEntity.ok(new ApiResponse<>(answer));
        } else {
            List<TypesDto> types = chatService.getAllTypeButtons();
            return ResponseEntity.ok(new ApiResponse<>(types));
        }
    }

}
