package com.twm.repository.admin.impl;

import com.twm.dto.ReturnSupportDto;
import com.twm.repository.admin.SupportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class SupportRepositoryImpl implements SupportRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<ReturnSupportDto> getSupportAll() {
        String sql = "SELECT * FROM support";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<ReturnSupportDto> supportDtos = namedParameterJdbcTemplate.query(sql, parameters, (RowMapper<ReturnSupportDto>) (rs, rowNum) -> {
            ReturnSupportDto supportDto = new ReturnSupportDto();
            supportDto.setId(rs.getLong("id"));
            supportDto.setName(rs.getString("name"));
            supportDto.setEmail(rs.getString("email"));
            supportDto.setDescription(rs.getString("description"));
            supportDto.setRequestTime(rs.getString("request_time"));
            return supportDto;
        });
        return (supportDtos.size() > 0) ? supportDtos : null;
    }
}
