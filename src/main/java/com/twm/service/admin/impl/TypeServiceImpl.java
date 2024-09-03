package com.twm.service.admin.impl;

import com.twm.dto.TypesDto;
import com.twm.repository.admin.TypeRepository;
import com.twm.service.admin.TypeService;
import com.twm.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TypeServiceImpl implements TypeService {

    private final TypeRepository typeRepository;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void saveType(TypesDto typesDto) {
        log.info("Data saved to the database. Cache will be cleared.");
        redisUtil.clearCache("type");
        typeRepository.saveType(typesDto);
    }

    @Override
    public Map<String, Object> getType(Integer id) {
        return Map.of("data", typeRepository.getType(id));
    }

    @Override
    public Map<String, Object> getTypeAll() {
        return Map.of("data", typeRepository.getTypeAll());
    }

    @Override
    public TypesDto updateType(TypesDto typesDto) {
        log.info("Data saved to the database. Cache will be cleared.");
        redisUtil.clearCache("type");
        return typeRepository.updateType(typesDto);
    }

    @Override
    public boolean deleteType(Long id) {
        log.info("Data saved to the database. Cache will be cleared.");
        redisUtil.clearCache("type");
        return typeRepository.deleteType(id);
    }

}
