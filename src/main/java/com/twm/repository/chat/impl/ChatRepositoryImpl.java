package com.twm.repository.chat.impl;

import com.twm.exception.custom.MissFieldException;
import com.twm.repository.chat.ChatRepository;
import com.twm.rowmapper.ButtonRowMapper;
import com.twm.rowmapper.CreateButtonRowMapper;
import com.twm.dto.*;
import com.twm.rowmapper.CategoryRowMapper;
import com.twm.rowmapper.TypeRowMapper;
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
    public List<String> getPersonality() {

        String sql = "SELECT description FROM personality";

        Map<String, Object> map = new HashMap<>();

        try {
            return namedParameterJdbcTemplate.query(sql, map, (rs, rowNum) -> rs.getString("description"));
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
