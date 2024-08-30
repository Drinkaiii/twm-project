package com.twm.controller;

import com.twm.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final JwtUtil jwtUtil;

    @GetMapping("/test")
    public String test(){
        // 以 Map 的形式放資料進去，可以得到 token
        Map<String,Object> map = new HashMap<>();
        map.put("username","test");
        String token = jwtUtil.getToken(map);
        System.out.println(token);
        // 藉由 token 取出資料
        Map<String,Object> map1 = jwtUtil.getClaims(token);
        System.out.println(map1.toString());
        // 驗證 token 有無過期
        System.out.println(jwtUtil.isTokenValid(token));
        return "done";
    }

}
