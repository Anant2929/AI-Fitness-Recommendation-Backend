package com.fitness.notification_service.consumer;

import com.fitness.notification_service.dto.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @RabbitListener(queues = "${fitness.rabbitmq.queue}")
    public void consumeEmailMessage(EmailDto emailDto){
        log.info("📩 Received Email Event for: {}", emailDto.getRecipientEmail());

        try {
            sendHtmlEmail(emailDto);
            log.info("✅ Email Sent Successfully to {}", emailDto.getRecipientEmail());
        } catch (Exception e) {
            log.error("❌ Failed to send email: {}", e.getMessage());
        }
    }

    private void sendHtmlEmail(EmailDto emailDto) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true , "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(emailDto.getRecipientEmail());
        helper.setSubject(emailDto.getSubject());
        helper.setText(emailDto.getBody() , true);

        mailSender.send(message);
    }




}
