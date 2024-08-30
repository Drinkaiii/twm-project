package com.twm.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twm.dto.error.ErrorResponseDto;
import com.twm.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("enter JwtFilter");
        String requestURI = request.getRequestURI();
        String[] excludedPaths = {
                "/api/1.0/user/signup",
                "/api/1.0/user/signin",
                "/api/1.0/user/update-authTime",
                "/captcha/image",
                "/.*\\.html",
                "/assets/.*"
        };

        for (String path : excludedPaths)
            if (requestURI.matches(path)) {
                filterChain.doFilter(request, response);
                return;
            }

        final String authHeader = request.getHeader("Authorization");
        System.out.println(authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Token validation error 1");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid token\"}");
            return;
        }

        final String token = authHeader.substring(7);

        log.info(token);

        try {

            if (token.isEmpty() || !jwtUtil.isTokenValid(token)) {
                log.error("Token validation error 2");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid token\"}");
//                filterChain.doFilter(request, response);
                return;
            }

            Map<String,Object> claims = jwtUtil.getClaims(token);

            log.info("claims : " + claims);




            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    claims,
                    null,
                    List.of()
            );
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);


        } catch (Exception e) {
            log.error("Token validation error", e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid token\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
