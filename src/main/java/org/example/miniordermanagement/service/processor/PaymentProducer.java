package org.example.miniordermanagement.service.processor;

import org.example.miniordermanagement.dto.PaymentResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentResultDto> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(PaymentProducer.class);

    public PaymentProducer(KafkaTemplate<String, PaymentResultDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(PaymentResultDto paymentResultDto) {
        try {
            logger.error("Attempting to send message to Kafka: {}", paymentResultDto);

            SendResult<String, PaymentResultDto> result = kafkaTemplate
                    .send("paymentResult", paymentResultDto)
                    .get(30, TimeUnit.SECONDS);

            logger.error("Message sent successfully to partition: {}, offset: {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

            System.out.println("Payment Result: " + paymentResultDto);
        } catch (Exception e) {
            logger.error("Failed to send message to Kafka", e);
            throw new RuntimeException("Could not produce message to kafka topic paymentResult: " + e.getMessage(), e);
        }
    }
}

