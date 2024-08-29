package com.twm.service.admin.impl;

import com.twm.dto.PersonalityDto;
import com.twm.repository.admin.PersonalityRepository;
import com.twm.service.admin.PersonalityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalityServiceImpl implements PersonalityService {

    private final PersonalityRepository personalityRepository;

    @Override
    public Map<String, Object> savePersonality(PersonalityDto personalityDto) {
        Map<String, Object> result = new HashMap<>();
        result.put("result", personalityRepository.savePersonality(personalityDto));

        return result;
    }

    @Override
    public Map<String, Object> getPersonality(Integer id) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", personalityRepository.getPersonality(id));

        return result;
    }

}
