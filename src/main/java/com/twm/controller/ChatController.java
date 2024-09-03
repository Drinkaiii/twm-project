package com.twm.controller;

import com.twm.dto.*;
import com.twm.generic.ApiResponse;
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
    public ResponseEntity<?> chat(@RequestBody RecordDto recordDto) {

        Long userId = recordDto.getUserId();
        String sessionId = recordDto.getSessionId();
        String question = recordDto.getQuestion();

        try {
            Map<String, Object> response = chatService.chat(userId, sessionId, question);

            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDto.error("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/routines")
    public ResponseEntity<ApiResponse<?>> getButtonsByParam(
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "type", required = false) Long typeId,
            @RequestParam(value = "question", required = false) Long buttonId) {

        if (typeId != null) {
            List<ReturnQuestionDto> buttons = chatService.getButtonsByType(typeId); //常見問答的問題按鈕
            return ResponseEntity.ok(new ApiResponse<>(buttons));
        } else if (buttonId != null) {
            String answer = chatService.getAnswerByQuestion(buttonId); //最終答案
            return ResponseEntity.ok(new ApiResponse<>(answer));
        } else if(categoryId != null && categoryId == 4) { //常見問答按鈕
            List<TypesDto> types = chatService.getAllTypeButtons();
            return ResponseEntity.ok(new ApiResponse<>(types));
        } else if(categoryId != null) {
            String url = chatService.getUrlByCategory(categoryId); //常見問答外，其他要拿網址重導向
            return ResponseEntity.ok(new ApiResponse<>(url));
        } else{
            List<ReturnCategoryDto> categories = chatService.getAllCategoryButtons(); //所有類別按鈕
            return ResponseEntity.ok(new ApiResponse<>(categories));
        }
    }

}
