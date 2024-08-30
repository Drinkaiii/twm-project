package com.twm.service.admin;

import com.twm.dto.CreateButtonDto;
import com.twm.dto.TypesDto;

import java.util.Map;

public interface TypeService {

    void saveType(TypesDto typesDto);

    Map<String, Object> getType(Integer id);

    TypesDto updateType(TypesDto typesDto);

    boolean deleteType(Long id);

}
