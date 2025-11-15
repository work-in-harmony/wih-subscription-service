package com.elu.wihsubscriptionservice.publisher;

import com.elu.wihsubscriptionservice.config.RabbitMqConfig;
import com.elu.wihsubscriptionservice.dto.RabbitMqRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailPublisher {
    private RabbitTemplate rabbitTemplate;

    public EmailPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    Logger LOGGER = LoggerFactory.getLogger(EmailPublisher.class);



    public void sendEmail(String email, String transcationId,  String amount) {
        RabbitMqRequestDto rabbitMqRequestDto = RabbitMqRequestDto.builder()
                .email(email)
                .transcationId(transcationId)
                .build();
        LOGGER.info("Sending email to {} with otp {}", email , transcationId);

        Map<String, String> map = new HashMap<>();
        map.put("transcationId", transcationId);
        map.put("amount", amount);
        map.put("email", email);
        String message = email + "+"  + transcationId + "+" + amount;
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_KEY,
                message);
    }
}
