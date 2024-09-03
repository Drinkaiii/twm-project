package com.twm.service.admin.impl;

import com.twm.dto.PersonalityDto;
import com.twm.repository.admin.PersonalityRepository;
import com.twm.service.admin.PersonalityService;
import com.twm.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalityServiceImpl implements PersonalityService {

    @Autowired
    private RedisUtil redisUtil;

    private final PersonalityRepository personalityRepository;

    private static final String CACHE_KEY = "personality";

    @Override
    public Map<String, Object> savePersonality(PersonalityDto personalityDto) {
        Map<String, Object> result = new HashMap<>();
        result.put("result", personalityRepository.savePersonality(personalityDto));

        return result;
    }

    @Override
    public Map<String, Object> getPersonality(Integer id) {

        Map<String, Object> result = new HashMap<>();
        boolean isCached = redisUtil.isCacheExist(CACHE_KEY);

        if (isCached) {
            log.info("Data retrieved from Redis cache.");
            result.put("data", redisUtil.getListDataFromCache(CACHE_KEY));
        } else {
            List<PersonalityDto> personalities = personalityRepository.getPersonality(id); //get sql data
            log.info("Data retrieved from the database.");

            redisUtil.setJsonDataToCache(CACHE_KEY, personalities);
            result.put("data", personalities);
        }

        return result;
    }

    @Override
    public PersonalityDto updatePersonality(PersonalityDto personalityDto) {
        return personalityRepository.updatePersonality(personalityDto);
    }

    @Override
    public boolean deletePersonality(Long id) {
        return personalityRepository.deletePersonality(id);
    }

}
