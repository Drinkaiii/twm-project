package com.twm.controller;

import com.google.code.kaptcha.Producer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ResponseBody
    @GetMapping("/image")
    public Map<String, Object> getCaptcha() throws IOException {
        // 生成驗證碼文字
        String captchaText = captchaProducer.createText();
        // 生成驗證碼圖片
        BufferedImage captchaImage = captchaProducer.createImage(captchaText);
        // 生成唯一ID
        String captchaId = UUID.randomUUID().toString();

        // 存入資料庫
        jdbcTemplate.update("INSERT INTO captcha (id, code) VALUES (?, ?)", captchaId, captchaText);

        // 將圖片轉換為Base64編碼（將圖片數據傳遞給前端）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(captchaImage, "png", baos);
        String base64Captcha = Base64.getEncoder().encodeToString(baos.toByteArray());

        // 返回captchaId和圖片數據
        Map<String, Object> result = new HashMap<>();
        result.put("captchaId", captchaId);
        result.put("captchaImage", "data:image/png;base64," + base64Captcha);
        return result;
    }

}

