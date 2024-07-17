package com.getourhome.agentservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAgencyNameChange(String agentId, String newAgencyName) {
        kafkaTemplate.send("agency-name-change", agentId + ":" + newAgencyName);
    }
}
