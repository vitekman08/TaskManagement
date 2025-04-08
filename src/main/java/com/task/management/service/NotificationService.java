package com.task.management.service;

import com.task.management.dto.TaskStatusUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendStatusChangeEmail(TaskStatusUpdateDto dto) {

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setFrom("tester-task08@yandex.ru");
        emailMessage.setTo("vitekman@mail.ru");
        emailMessage.setSubject("Статус задачи изменен");
        emailMessage.setText("Статус задачи с ID " + dto.getId() + " изменен на " + dto.getStatus());
        try {
            mailSender.send(emailMessage);
            log.info("Email отправлен пользователю по задаче с ID " + dto.getId());
        } catch (Exception e) {
            log.error("Ошибка отправки email {}", e.getMessage(), e);
        }

    }
}
