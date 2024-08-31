package com.twm.service.admin.impl;

import com.twm.repository.admin.SupportRepository;
import com.twm.service.admin.SupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupportServiceImpl implements SupportService {

    private final SupportRepository supportRepository;

    @Override
    public Map<String, Object> getSupportAll() {
        return Map.of("data", supportRepository.getSupportAll());
    }

    ;
}
