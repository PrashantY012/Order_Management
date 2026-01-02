package org.example.miniordermanagement.service.processor;
import org.example.miniordermanagement.dto.PaymentResultDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    @KafkaListener(topics = "paymentResult", groupId = "payment-group")
    public void consume(PaymentResultDto paymentResultDto) {
        System.out.println("Received payment: " + paymentResultDto);
        // You can save it, process it, or trigger other logic


    }
}
