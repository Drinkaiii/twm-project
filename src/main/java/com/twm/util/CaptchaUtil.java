package com.twm.util;

import com.google.code.kaptcha.Producer;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY;

@Component
public class CaptchaUtil {

    private Producer verifyCodeProducer;

    public void getCaptchaImage(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("image/jpeg"); // response type
        String capText = verifyCodeProducer.createText(); // produce random text
        BufferedImage bi = verifyCodeProducer.createImage(capText) ; // produce random pic

        HttpSession session = request.getSession();
        session.setAttribute(KAPTCHA_SESSION_KEY, capText); //save random text into session

        ServletOutputStream out = null;

        out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out); // save pic into response
        out.flush();  // ensure that all pic has been written into response, nothing left in buffer

        IOUtils.closeQuietly(out);

    }

}
