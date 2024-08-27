package com.twm.rowmapper;

import com.twm.dto.CategoriesDto;
import com.twm.dto.ReturnCategoryDto;
import com.twm.dto.ReturnQuestionDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryRowMapper implements RowMapper<ReturnCategoryDto> {

    @Override
    public ReturnCategoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReturnCategoryDto button = new ReturnCategoryDto();
        button.setId(rs.getLong("id"));
        button.setCategory(rs.getString("category"));
        return button;
    }

}
