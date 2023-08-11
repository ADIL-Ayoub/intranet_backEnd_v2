package com.procheck.intranet.services.specifications;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail,String subject,String body){
        SimpleMailMessage message= new SimpleMailMessage();
        message.setFrom("samih287@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        log.info("[MAIL SERVICE] - [ SENDING MAIL TO :"+toEmail+" ]");
        mailSender.send(message);
        log.info("[MAIL SERVICE] - [ MAIL IS SENT TO :"+toEmail+" ]");
    }


}
