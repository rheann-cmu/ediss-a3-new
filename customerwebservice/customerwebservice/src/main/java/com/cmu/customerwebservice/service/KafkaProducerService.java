package com.cmu.customerwebservice.service;

import com.cmu.customerwebservice.entities.UserMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final ObjectMapper objectMapper;

    /*@Autowired
    public KafkaProducerService(KafkaTemplate<String, UserMessage> kafkaTemplate,
                                @Value("${kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }*/

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                @Value("${kafka.topic}") String topic, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.objectMapper = objectMapper;
    }

    public void produce(UserMessage userMessage) {
        try {
            String userMessageJson = objectMapper.writeValueAsString(userMessage);
            kafkaTemplate.send(topic, userMessageJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
