package com.twm.repository.chat.impl;

import com.twm.exception.custom.MissFieldException;
import com.twm.repository.chat.ChatRepository;
import com.twm.rowmapper.*;
import com.twm.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.jdbc.core.JdbcTemplate;

@Repository
@Transactional
@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepository {

    private static final Logger log = LoggerFactory.getLogger(ChatRepositoryImpl.class);
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TypesDto> findAllTypeButtons() {
        String sql = "SELECT * FROM types";
        try{
            return jdbcTemplate.query(sql, new TypeRowMapper());
        }catch (DataAccessException e){
            return null;
        }
    }

    @Override
    public List<ReturnQuestionDto> findButtonsByType(Long typeId) {
        String sql = "SELECT * FROM buttons WHERE type_id = ?";
        try {
            return jdbcTemplate.query(sql, new Object[]{typeId}, new ButtonRowMapper());
        }catch (DataAccessException e){
            return null;
        }
    }

    @Override
    public String findAnswerByQuestion(Long buttonId) {
        String sql = "SELECT answer FROM buttons WHERE id = ?";
        try{
            return jdbcTemplate.queryForObject(sql, new Object[]{buttonId}, String.class);
        }catch (DataAccessException e){
            return null;
        }
    }

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

            if(createButtonDto.getId() == null) {
                updateMap.put("id", origin.getId());
            }else {
                updateMap.put("id", createButtonDto.getId());
            }

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

    @Override
    public List<String> getSessionHistory(String sessionId) {

        String sql = "SELECT question,response FROM records WHERE session_id = :sessionId";

        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", sessionId);

        try {
            return namedParameterJdbcTemplate.query(sql, map, (rs, rowNum) ->
                    rs.getString("question") + ": " + rs.getString("response"));
        }catch (DataAccessException e){
            throw new RuntimeException("Failed to load history", e);
        }

    }

    @Override
    public void saveSession(Long userId, String sessionId, String question, String responseContent) {

        String sql = "INSERT INTO records(question, response, user_id, session_id) VALUES (:question, :responseContent, :userId, :sessionId);";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("question", question);
        map.put("responseContent", responseContent);
        map.put("userId", userId);
        map.put("sessionId", sessionId);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        }catch (DataAccessException e){
            throw new RuntimeException("Failed to save session", e);
        }

    }

    @Override
    public List<String> getFAQ() {

        String sql = "SELECT question,answer FROM buttons";

        Map<String, Object> map = new HashMap<>();

        try {
            return namedParameterJdbcTemplate.query(sql, map, (rs, rowNum) ->
                    rs.getString("question") + ": " + rs.getString("answer"));
        }catch (DataAccessException e){
            throw new RuntimeException("Failed to load FAQ", e);
        }

    }

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
                throw new MissFieldException("The answer of question don't exist");
            }
        }catch (DataAccessException e){
            throw new RuntimeException("Failed to load personality", e);
        }

    }

    @Override
    public List<ReturnCategoryDto> findAllCategoryButtons(){
        String sql = "SELECT * FROM categories";
        try{
            return jdbcTemplate.query(sql, new CategoryRowMapper());
        }catch (DataAccessException e){
            return null;
        }
    };

    @Override
    public String findUrlByCategory(Long categoryId){
        String sql = "SELECT url FROM categories WHERE id = ?";
        try{
            return jdbcTemplate.queryForObject(sql, new Object[]{categoryId}, String.class);
        }catch (DataAccessException e){
            return null;
        }
    }
}
