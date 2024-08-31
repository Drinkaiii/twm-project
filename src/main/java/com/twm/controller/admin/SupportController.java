package com.twm.controller.admin;

import com.twm.service.admin.SupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/admin/support")
@Slf4j
public class SupportController {

    private final SupportService supportService;

    @GetMapping("/review")
    public ResponseEntity<?> supportReview() {
        return ResponseEntity.ok(supportService.getSupportAll());
    }
}
