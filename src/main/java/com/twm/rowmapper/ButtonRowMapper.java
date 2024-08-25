package com.twm.rowmapper;

import com.twm.dto.ButtonDto;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ButtonRowMapper implements RowMapper<ButtonDto>  {

    @Override
    public ButtonDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ButtonDto button = new ButtonDto();
        button.setId(rs.getLong("id"));
        button.setType(rs.getString("type"));
        button.setQuestion(rs.getString("question"));
        button.setAnswer(rs.getString("answer"));
        return button;
    }

}
