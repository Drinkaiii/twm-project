package com.twm.rowmapper;

import com.twm.dto.ButtonDto;
import com.twm.dto.CreateButtonDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateButtonRowMapper implements RowMapper<CreateButtonDto> {

    @Override
    public CreateButtonDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        CreateButtonDto button = new CreateButtonDto();
        button.setId(rs.getLong("id"));
        button.setType(rs.getLong("type_id"));
        button.setQuestion(rs.getString("question"));
        button.setAnswer(rs.getString("answer"));
        return button;
    }

}
