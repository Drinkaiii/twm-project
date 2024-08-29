package com.twm.service.admin;

import com.twm.dto.CreateButtonDto;
import com.twm.dto.PersonalityDto;

import java.util.Map;

public interface ButtonService {

    void saveButton(CreateButtonDto createButtonDto);
    Map<String, Object> getButton(Integer id);
    CreateButtonDto updateButton(CreateButtonDto createButtonDto);
    boolean deleteButton(Long id);

}
