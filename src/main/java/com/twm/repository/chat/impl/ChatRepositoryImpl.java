package com.twm.repository.chat.impl;

import com.twm.dto.ButtonDto;
import com.twm.repository.chat.ChatRepository;
import com.twm.rowmapper.ButtonRowMapper;
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
import org.springframework.jdbc.core.JdbcTemplate;

@Repository
@Transactional
@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ButtonDto> findAllButtons() {
        String sql = "SELECT * FROM buttons";
        try {
            return jdbcTemplate.query(sql, new ButtonRowMapper());
        }
        catch (DataAccessException e) {
            return null;
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
            return null;
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
            return null;
        }

    }

}
