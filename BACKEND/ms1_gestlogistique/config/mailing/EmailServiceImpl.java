package com.groupeisi.ms1_gestschool.config.mailing;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    public void envoyerMailing(String[] to, String subject, String body, File pieceJointe) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        // 'true' indique qu'on veut un message multipart (pour la PJ)
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        // Ajout de la pièce jointe
        if (pieceJointe != null && pieceJointe.exists()) {
            helper.addAttachment(pieceJointe.getName(), pieceJointe);
        }

        mailSender.send(message);
    }
}
