package com.twm.service.admin.impl;

import com.twm.dto.CreateButtonDto;
import com.twm.repository.admin.ButtonRepository;
import com.twm.service.admin.ButtonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ButtonServiceImpl implements ButtonService {

    private final ButtonRepository buttonRepository;

    @Override
    public void saveButton(CreateButtonDto createButtonDto){

        buttonRepository.saveButton(createButtonDto);

//        Map<String, Object> result = new HashMap<>();
//        result.put("data", chatRepository.saveButton(createButtonDto));

//        return result;
    }

    @Override
    public Map<String, Object> getButton(Integer id){

        Map<String, Object> result = new HashMap<>();
        result.put("data", buttonRepository.getButton(id));

        return result;

    }

    @Override
    public CreateButtonDto updateButton(CreateButtonDto createButtonDto) {
        return buttonRepository.updateButton(createButtonDto);
    }

    @Override
    public boolean deleteButton(Long id) {
        return buttonRepository.deleteButton(id);
    }

}
