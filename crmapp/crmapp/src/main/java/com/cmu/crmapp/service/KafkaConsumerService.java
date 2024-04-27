package com.cmu.crmapp.service;

import com.cmu.crmapp.entity.UserMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;
    private final String topic;

    @Autowired
    public KafkaConsumerService(ObjectMapper objectMapper, EmailService emailService, @Value("${kafka.topic}") String topic) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
        this.topic = topic;
    }

    @KafkaListener(topics = "${kafka.topic}")
    public void consume(String message) {
        System.out.println("Received message: " + message);

        try {
            // Deserialize the JSON string
            UserMessage userMessage = objectMapper.readValue(message, UserMessage.class);
            String userId = userMessage.getUserId();
            String name = userMessage.getName();

            // Check if both userId and name are present
            if (userId != null && name != null) {
                // Call the sendEmail method of EmailService
                emailService.sendEmail(userId, name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

