package com.twm.repository.admin;

import com.twm.dto.CreateButtonDto;
import com.twm.dto.TypesDto;

import java.util.List;

public interface TypeRepository {

    void saveType(TypesDto typesDto);

    List<TypesDto> getType(Integer id);

    List<TypesDto> getTypeAll();

    TypesDto updateType(TypesDto typesDto);

    boolean deleteType(Long id);

}
