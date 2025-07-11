package com.example.simplezakka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env; // application.propertiesから値を取得

    public void sendResetLink(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("【シンプル雑貨オンライン】パスワード再設定リンク");
        message.setText(
            "以下のリンクからパスワードを再設定してください。\n\n" +
            resetLink + "\n\n" +
            "※このリンクは15分間有効です。\n\n" +
            "ご不明な点がある場合はサポートまでご連絡ください。"
        );
        message.setFrom(env.getProperty("spring.mail.username")); // 差出人

        mailSender.send(message);
    }
}
