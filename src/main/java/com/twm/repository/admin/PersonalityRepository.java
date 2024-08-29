package com.twm.repository.admin;

import com.twm.dto.CreateButtonDto;
import com.twm.dto.PersonalityDto;

import java.util.List;

public interface PersonalityRepository {

    boolean savePersonality(PersonalityDto personalityDto);
    List<PersonalityDto> getPersonality(Integer id);
    PersonalityDto updatePersonality(PersonalityDto personalityDto);
    boolean deletePersonality(Long id);

}
