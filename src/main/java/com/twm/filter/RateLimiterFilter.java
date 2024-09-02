package com.twm.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimiterFilter extends OncePerRequestFilter {

    final private RedisTemplate redisTemplate;
    @Value("${MAX_REQUEST_LIMIT}")
    private int MAX_REQUEST_LIMIT;
    @Value("${PER_TIME_SECOND}")
    private int PER_TIME_SECOND;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String key = ip + ":" + uri;
        if (isExceedSpeed(key)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests");
            return;
        }
        filterChain.doFilter(request, response);
    }

    public Boolean isExceedSpeed(String ip) {
        try {
            Long currentCount = redisTemplate.opsForValue().increment(ip);
            if (currentCount != null && currentCount == 1) {
                redisTemplate.expire(ip, PER_TIME_SECOND, TimeUnit.SECONDS);
            }
            return currentCount != null && currentCount > MAX_REQUEST_LIMIT;

        } catch (RedisConnectionFailureException | QueryTimeoutException e) {
            log.error("Redis connection failure", e);
            return false;
        }
    }

}
