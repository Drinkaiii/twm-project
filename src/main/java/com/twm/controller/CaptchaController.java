package com.twm.controller;

import com.google.code.kaptcha.Producer;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY;


@Controller
@RequiredArgsConstructor
@RequestMapping("/captcha")
public class CaptchaController {

    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);
    private final Producer verifyCodeProducer;

    @GetMapping("/image")
    public void getCaptchaImage(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("image/jpeg"); // response type
        String capText = verifyCodeProducer.createText(); // produce random text
        BufferedImage bi = verifyCodeProducer.createImage(capText) ; // produce random pic

        HttpSession session = request.getSession();
        session.setAttribute(KAPTCHA_SESSION_KEY, capText); //save random text into session
        log.info("capText:"+capText);
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out); // save pic into response
            out.flush();  // ensure that all pic has been written into response, nothing left in buffer
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

}

