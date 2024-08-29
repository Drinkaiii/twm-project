package com.twm.repository.admin.impl;

import com.twm.dto.PersonalityDto;
import com.twm.exception.custom.MissFieldException;
import com.twm.repository.admin.PersonalityRepository;
import com.twm.rowmapper.PersonalityRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
@RequiredArgsConstructor
public class PersonalityRepositoryImpl implements PersonalityRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public boolean savePersonality(PersonalityDto personalityDto) {
        String sql = "INSERT INTO personality(description) VALUES (:description);";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("description", personalityDto.getDescription());

        if(personalityDto.getDescription().isEmpty()) {
            throw new MissFieldException("Failed to save personality");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            Integer result = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

            if(result > 0) {
                return true;
            }else {
                return false;
            }
        }catch (DataAccessException e){
            throw new RuntimeException("Failed to save personality", e);
        }
    }

    @Override
    public List<PersonalityDto> getPersonality(Integer id) {

        String sql = "";

        Map<String, Object> map = new HashMap<>();

        if(id > 0) {
            sql = "SELECT * FROM personality WHERE id = :id;";
            map.put("id", id);
        }else{
            sql = "SELECT * FROM personality";
        }

        try {
            List<PersonalityDto> personalityList = namedParameterJdbcTemplate.query(sql, map, new PersonalityRowMapper());
            if(!personalityList.isEmpty()){
                return personalityList;
            }else {
                throw new MissFieldException("The personality don't exist");
            }
        }catch (DataAccessException e){
            throw new RuntimeException("Failed to load personality", e);
        }

    }

}
