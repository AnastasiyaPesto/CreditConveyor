package ru.zentsova.Dossier.services.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.zentsova.Dossier.services.EmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    public void sendMessage(String from, String to, String subject, String body) {
        SimpleMailMessage message = (SimpleMailMessage) createMessage(from, to, subject, body);
        try {
            emailSender.send(message);
            log.info("Message >>> from {}, to {}, subject {}, body {}", from, to, subject, body);
        } catch (MailException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        }
    }

    public MailMessage createMessage(String from, String to, String subject, String text) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        LocalDateTime now = LocalDateTime.now();
        Date date = convertLocalDateTimeToDate(now);
        message.setSentDate(date);
        log.info("Message created at {} from {}, to {}, subject {}, body {}", now, from, to, subject, text);

        return message;
    }

    private Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}