package com.twm.controller.admin;

import com.twm.dto.CreateButtonDto;
import com.twm.dto.TypesDto;
import com.twm.dto.error.ErrorResponseDto;
import com.twm.exception.custom.MissFieldException;
import com.twm.service.admin.TypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/admin/type")
@Slf4j
public class TypeController {

    private final TypeService typeService;

    @PostMapping("/create")
    public ResponseEntity<?> chatCreate(@RequestBody TypesDto typesDto) {
        typeService.saveType(typesDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/review")
    public ResponseEntity<?> chatReview(Integer id) {
        return ResponseEntity.ok(typeService.getType(id));
    }

    @PostMapping("/update")
    public ResponseEntity<?> chatUpdate(@RequestBody TypesDto typesDto) {
        return ResponseEntity.ok(typeService.updateType(typesDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> chatDelete(@RequestBody TypesDto typesDto) {
        return typeService.deleteType(typesDto.getId()) ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissFieldException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleMissFieldException(MissFieldException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ErrorResponseDto.error(e.getMessage()), HttpStatus.NOT_FOUND);
    }

}
