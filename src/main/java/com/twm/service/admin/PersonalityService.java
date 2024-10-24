package com.twm.service.admin;

import com.twm.dto.CreateButtonDto;
import com.twm.dto.PersonalityDto;

import java.util.Map;

public interface PersonalityService {

    Map<String, Object> savePersonality(PersonalityDto personalityDto);
    Map<String, Object> getPersonality(Integer id);
    PersonalityDto updatePersonality(PersonalityDto personalityDto);
    boolean deletePersonality(Long id);

}
