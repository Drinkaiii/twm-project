package com.twm.rowmapper;

import com.twm.dto.ReturnQuestionDto;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ButtonRowMapper implements RowMapper<ReturnQuestionDto>  {

    @Override
    public ReturnQuestionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReturnQuestionDto button = new ReturnQuestionDto();
        button.setId(rs.getLong("id"));
        button.setQuestion(rs.getString("question"));
        return button;
    }

}
