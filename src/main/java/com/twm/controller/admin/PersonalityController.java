package com.twm.controller.admin;

import com.twm.dto.PersonalityDto;
import com.twm.dto.error.ErrorResponseDto;
import com.twm.exception.custom.MissFieldException;
import com.twm.service.admin.PersonalityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/admin/personality")
@Slf4j
public class PersonalityController {

    private final PersonalityService personalityService;

    @PostMapping("/create")
    public ResponseEntity<?> personalityCreate(@RequestBody PersonalityDto personalityDto) {

        try {
            Map<String, Object> response = personalityService.savePersonality(personalityDto);

            return ResponseEntity.ok(response);
        }catch (MissFieldException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (RuntimeException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/review")
    public ResponseEntity<?> personalityReview(@RequestParam(value = "id", defaultValue = "0") Integer id) {

        try {
            Map<String, Object> response = personalityService.getPersonality(id);

            return ResponseEntity.ok(response);
        }catch (MissFieldException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (RuntimeException e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

}
