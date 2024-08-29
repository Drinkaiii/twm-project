package com.twm.rowmapper;

import com.twm.dto.PersonalityDto;
import com.twm.dto.ReturnQuestionDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonalityRowMapper implements RowMapper<PersonalityDto> {

    @Override
    public PersonalityDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        PersonalityDto personality = new PersonalityDto();
        personality.setId(rs.getLong("id"));
        personality.setDescription(rs.getString("description"));
        return personality;
    }

}
