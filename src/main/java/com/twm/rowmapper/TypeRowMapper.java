package com.twm.rowmapper;

import com.twm.dto.TypesDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TypeRowMapper implements RowMapper<TypesDto> {

    @Override
    public TypesDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        TypesDto type = new TypesDto();
        type.setId(rs.getLong("id"));
        type.setTypeName(rs.getString("type_name"));
        return type;
    }
}
