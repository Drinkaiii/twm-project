package com.twm.repository.chat.impl;

import com.twm.dto.ButtonDto;
import com.twm.dto.TypesDto;
import com.twm.repository.chat.ChatRepository;
import com.twm.rowmapper.ButtonRowMapper;
import com.twm.rowmapper.TypeRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TypesDto> findAllTypeButtons() {
        String sql = "SELECT * FROM types";
        return jdbcTemplate.query(sql, new TypeRowMapper());
    }

    @Override
    public List<ButtonDto> findButtonsByType(Long typeId) {
        String sql = "SELECT * FROM buttons WHERE type_id = ?";
        return jdbcTemplate.query(sql, new Object[]{typeId}, new ButtonRowMapper());
    }

    @Override
    public String findAnswerByQuestion(Long buttonId) {
        String sql = "SELECT answer FROM buttons WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{buttonId}, String.class);
    }

}
