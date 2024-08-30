package com.twm.repository.user.impl;

import com.twm.dto.UserDto;
import com.twm.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
@Transactional
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Integer createNativeUser(UserDto userDto){
        try {
            String sql = "INSERT INTO users (email,password,provider) VALUES (:email,:password,:provider)";
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            Map<String, Object> map = new HashMap<>();
            map.put("email", userDto.getEmail());
            map.put("password", encodedPassword);
            map.put("provider", userDto.getProvider());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            SqlParameterSource paramSource = new MapSqlParameterSource(map);
            namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
            return keyHolder.getKey().intValue();
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDto getNativeUserByEmailAndProvider(String email){
        try {
            String sql = "SELECT * FROM users WHERE email = :email AND provider = :provider";
            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("provider", "native");
            SqlParameterSource paramSource = new MapSqlParameterSource(map);
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(UserDto.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDto getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(UserDto.class));
        } catch (DataAccessException e) {
            throw e;
        }
    }

    @Override
    public UserDto updatePasswordByEmail(UserDto userDto) {
        String sql = "UPDATE users SET password = :password WHERE email = :email";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("email", userDto.getEmail());
        parameters.addValue("password", userDto.getPassword());
        int rowsAffected = namedParameterJdbcTemplate.update(sql, parameters);
        if (rowsAffected > 0) {
            return userDto;
        } else {
            throw new RuntimeException("Failed to update password for email: " + userDto.getEmail());
        }
    }

    @Override
    public int updateAuthTimeByUserId(String userId, String authTime) {
        String sql = "UPDATE users SET auth_time = :authTime WHERE id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("authTime", authTime);
        return namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public UserDto getTwmUserByEmailAndProvider(String email){
        try {
            String sql = "SELECT * FROM users WHERE email = :email AND provider = :provider";
            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("provider", "twm");
            SqlParameterSource paramSource = new MapSqlParameterSource(map);
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(UserDto.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Integer createTwmUser (String email){
        try {
            String sql = "INSERT INTO users (email,password,provider) VALUES (:email,:password,:provider)";
            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("password", "");
            map.put("provider", "twm");
            KeyHolder keyHolder = new GeneratedKeyHolder();
            SqlParameterSource paramSource = new MapSqlParameterSource(map);
            namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
            return keyHolder.getKey().intValue();
        }
        catch (DataAccessException e) {
            log.error("user dao:"+e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
