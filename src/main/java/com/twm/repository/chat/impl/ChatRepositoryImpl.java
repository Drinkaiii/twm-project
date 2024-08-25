package com.twm.repository.chat.impl;

import com.twm.dto.ButtonDto;
import com.twm.repository.chat.ChatRepository;
import com.twm.rowmapper.ButtonRowMapper;
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
    public List<ButtonDto> findAllButtons() {
        String sql = "SELECT * FROM buttons";
        return jdbcTemplate.query(sql, new ButtonRowMapper());
    }

}
