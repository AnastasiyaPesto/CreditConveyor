package ru.zentsova.Dossier.services.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.zentsova.Dossier.model.EmailMsgTemplate;
import ru.zentsova.Dossier.services.EmailService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    public SimpleMailMessage createSimpleMessage(EmailMsgTemplate emailMsgTemplate) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailMsgTemplate.getFrom());
        message.setTo(emailMsgTemplate.getTo());
        message.setSubject(emailMsgTemplate.getSubject());
        message.setText(emailMsgTemplate.getBody());
        LocalDateTime now = LocalDateTime.now();
        Date date = convertLocalDateTimeToDate(now);
        message.setSentDate(date);

        emailMsgTemplate.setDate(now);
        writeLogInfo("Message created at {} from {}, to {}, subject {}, body {}", emailMsgTemplate);

        return message;
    }

    public void sendSimpleMessage(EmailMsgTemplate emailMsgTemplate) {
        final SimpleMailMessage message = createSimpleMessage(emailMsgTemplate);
        try {
            emailSender.send(message);
            writeLogInfo("Message >>> at {} from {}, to {}, subject {}, body {}", emailMsgTemplate);
        } catch (MailException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        }
    }

    public MimeMessage createMimeMessage(EmailMsgTemplate emailMsgTemplate, boolean html) {
        final MimeMessage message = emailSender.createMimeMessage();
        try {
            final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
            mimeMessageHelper.setFrom(emailMsgTemplate.getFrom());
            mimeMessageHelper.setTo(emailMsgTemplate.getTo());
            mimeMessageHelper.setSubject(emailMsgTemplate.getTo());
            mimeMessageHelper.setText(emailMsgTemplate.getBody(), html);
            LocalDateTime now = LocalDateTime.now();
            Date date = convertLocalDateTimeToDate(now);
            mimeMessageHelper.setSentDate(date);

            emailMsgTemplate.setDate(now);
            writeLogInfo("Message created at {} from {}, to {}, subject {}, body {}", emailMsgTemplate);
        } catch (MessagingException ex) {
            log.error("Failed to send message", ex);
            throw new IllegalStateException(ex);
        }

        return message;
    }

    public void sendMimeMessage(EmailMsgTemplate emailMsgTemplate, boolean html)  {
        final MimeMessage message = createMimeMessage(emailMsgTemplate, html);
        try {
            emailSender.send(message);
            writeLogInfo("Message >>> at {} from {}, to {}, subject {}, body {}", emailMsgTemplate);
        } catch (MailException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        }
    }

    private void writeLogInfo(String text, EmailMsgTemplate params) {
        log.info(text, params.getDate(), params.getFrom(), params.getTo(), params.getSubject(), params.getBody());
    }

    private Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}