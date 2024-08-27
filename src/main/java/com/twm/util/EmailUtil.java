package com.twm.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailUtil {

    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;
    @Value("${spring.mail.username}")
    private String from;

    public void sendEmail(String to, String subject, String resetLink) {

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject);
            Resource resource = resourceLoader.getResource("classpath:templates/email-reset-password.html");
            String htmlTemplate = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            String htmlContent = htmlTemplate.replace("{{username}}", to)
                    .replace("{{resetLink}}", resetLink);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("The email has been sent to " + to);

        } catch (MailSendException e) {
            log.warn("Mail server connection failed.");
            e.printStackTrace();
        } catch (MailAuthenticationException e) {
            log.warn("Mail server Authentication failed.");
            e.printStackTrace();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}