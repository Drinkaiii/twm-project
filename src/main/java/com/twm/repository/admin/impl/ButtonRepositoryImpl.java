package com.twm.repository.admin.impl;

import com.twm.dto.CreateButtonDto;
import com.twm.exception.custom.MissFieldException;
import com.twm.repository.admin.ButtonRepository;
import com.twm.rowmapper.CreateButtonRowMapper;
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
public class ButtonRepositoryImpl implements ButtonRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void saveButton(CreateButtonDto createButtonDto) {
        String sql = "INSERT INTO buttons(type_id, question, answer) VALUES (:typeId, :question, :answer);";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("typeId", createButtonDto.getType());
        map.put("question", createButtonDto.getQuestion());
        map.put("answer", createButtonDto.getAnswer());

        if(createButtonDto.getQuestion().isEmpty() || createButtonDto.getAnswer().isEmpty()) {
            throw new MissFieldException("Failed to save button");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

//            int buttonId = keyHolder.getKey().intValue();
//
//            return buttonId;
        }catch (DataAccessException e){
            throw new RuntimeException("Failed to save button", e);
        }
    }

    @Override
    public List<CreateButtonDto> getButton(Integer id) {

        String sql = "";
        Map<String, Object> map = new HashMap<String, Object>();

        if(id > 0) {
            sql = "SELECT * FROM buttons WHERE id = :id;";
            map.put("id", id);
        }else{
            sql = "SELECT * FROM buttons;";
        }

        try {
            List<CreateButtonDto> buttonList = namedParameterJdbcTemplate.query(sql, map, new CreateButtonRowMapper());
            if(!buttonList.isEmpty()){
                return buttonList;
            }else {
                throw new MissFieldException("The answer of question don't exist");
            }
        }catch (DataAccessException e){
            throw new RuntimeException("Failed to get button", e);
        }
    }

    @Override
    public CreateButtonDto updateButton(CreateButtonDto createButtonDto) {

        try {

            String selectOriginSql = "SELECT * FROM buttons WHERE id = :id;";
            Map<String, Object> selectOriginMap = new HashMap<String, Object>();
            selectOriginMap.put("id", createButtonDto.getId());

            CreateButtonDto origin = namedParameterJdbcTemplate.queryForObject(selectOriginSql, selectOriginMap, new CreateButtonRowMapper());

            String updateSql = "UPDATE buttons " +
                    "SET type_id = :typeId, question = :question, answer = :answer " +
                    "WHERE id = :id;";

            Map<String, Object> updateMap = new HashMap<String, Object>();

            if(createButtonDto.getType() == null) {
                updateMap.put("typeId", origin.getType());
            }else {
                updateMap.put("typeId", createButtonDto.getType());
            }

            if(createButtonDto.getQuestion() == null) {
                updateMap.put("question", origin.getQuestion());
            }else {
                updateMap.put("question", createButtonDto.getQuestion());
            }

            if(createButtonDto.getAnswer() == null) {
                updateMap.put("answer", origin.getAnswer());
            }else {
                updateMap.put("answer", createButtonDto.getAnswer());
            }

            KeyHolder keyHolder = new GeneratedKeyHolder();

            namedParameterJdbcTemplate.update(updateSql, new MapSqlParameterSource(updateMap), keyHolder);

            String selectSql = "SELECT * FROM buttons WHERE id = :id;";
            Map<String, Object> selectMap = new HashMap<String, Object>();
            selectMap.put("id", createButtonDto.getId());

            return namedParameterJdbcTemplate.queryForObject(selectSql, selectMap, new CreateButtonRowMapper());

        }catch (DataAccessException e){
            throw new RuntimeException("Failed to update button", e);
        }
    }

    @Override
    public boolean deleteButton(Long id) {
        String sql = "DELETE FROM buttons WHERE id = :id;";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        Integer result = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        if(result > 0) {
            return true;
        }else {
            return false;
        }
    }

}
