package com.twm.repository.admin.impl;

import com.twm.dto.TypesDto;
import com.twm.repository.admin.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class TypeRepositoryImpl implements TypeRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void saveType(TypesDto typesDto) {
        String sql = "INSERT INTO types(type_name) VALUES (:type_name);";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        parameters.addValue("type_name", typesDto.getTypeName());
        namedParameterJdbcTemplate.update(sql, parameters, keyHolder, new String[]{"id"});
    }

    @Override
    public List<TypesDto> getType(Integer id) {
        String sql = "SELECT * FROM types WHERE id = :id;";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<TypesDto> typesDtos = namedParameterJdbcTemplate.query(sql, parameters, (RowMapper<TypesDto>) (rs, rowNum) -> {
            TypesDto typesDto = new TypesDto();
            typesDto.setId(rs.getLong("id"));
            typesDto.setTypeName(rs.getString("type_name"));
            return typesDto;
        });
        return (typesDtos.size() > 0) ? typesDtos : null;
    }

    @Override
    public List<TypesDto> getTypeAll() {
        String sql = "SELECT * FROM types";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<TypesDto> typesDtos = namedParameterJdbcTemplate.query(sql, parameters, (RowMapper<TypesDto>) (rs, rowNum) -> {
            TypesDto typesDto = new TypesDto();
            typesDto.setId(rs.getLong("id"));
            typesDto.setTypeName(rs.getString("type_name"));
            return typesDto;
        });
        return typesDtos != null ? typesDtos : new ArrayList<>();
    }

    @Override
    public TypesDto updateType(TypesDto typesDto) {
        String sql = "UPDATE types SET type_name = :type_name WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", typesDto.getId());
        parameters.addValue("type_name", typesDto.getTypeName());
        int rowsAffected = namedParameterJdbcTemplate.update(sql, parameters);
        if (rowsAffected > 0)
            return typesDto;
        else
            throw new RuntimeException("Failed to update record with id : " + typesDto.getId());
    }

    @Override
    public boolean deleteType(Long id) {
        String sql = "DELETE FROM types WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        int rowsAffected = namedParameterJdbcTemplate.update(sql, parameters);
        if (rowsAffected > 0)
            return true;
        else
            throw new RuntimeException("Failed to delete record with id: " + id);
    }

    @ExceptionHandler(DataAccessException.class)
    public void handleRuntimeException(DataAccessException e) {
        throw new RuntimeException("Failed to save button", e);
    }

}
