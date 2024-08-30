package com.twm.repository.admin;

import com.twm.dto.CreateButtonDto;

import java.util.List;

public interface TypeRepository {

    void saveButton(CreateButtonDto createButtonDto);
    List<CreateButtonDto> getButton(Integer id);
    CreateButtonDto updateButton(CreateButtonDto createButtonDto);
    boolean deleteButton(Long id);

}
