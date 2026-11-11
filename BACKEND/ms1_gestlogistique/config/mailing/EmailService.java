package com.groupeisi.ms1_gestschool.config.mailing;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public interface EmailService {
    void envoyerMailing(String[] to, String subject, String body, File pieceJointe) throws MessagingException;
}