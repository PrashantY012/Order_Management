package org.example.miniordermanagement.service;
import org.example.miniordermanagement.service.processor.PaymentProcessor;
import org.example.miniordermanagement.service.processor.PaymentProducer;
import org.example.miniordermanagement.dto.PaymentDto;
import org.example.miniordermanagement.dto.PaymentResultDto;
import org.example.miniordermanagement.enums.PaymentMode;
import org.example.miniordermanagement.models.Payment;
import org.example.miniordermanagement.repository.PaymentRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepo paymentRepo;
    public final Map<String, PaymentProcessor> processors;
    private final OrderService orderService;
    private final PaymentProducer paymentProducer;


    public PaymentService(PaymentRepo paymentRepo, Map<String, PaymentProcessor> processors, OrderService orderService, PaymentProducer paymentProducer) {
        this.paymentRepo = paymentRepo;
        this.processors = processors;
        this.orderService = orderService;
        this.paymentProducer = paymentProducer;
    }

    PaymentDto getPaymentDto(Payment payment) {
        return new PaymentDto(payment);
    }

    public PaymentDto updatePaymentStatus(PaymentDto paymentDto) {
        Payment payment = paymentRepo.findById(paymentDto.getId()).orElse(null);
        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }
        payment.setStatus(paymentDto.getStatus());
        Payment updatedPayment = paymentRepo.save(payment);
        return getPaymentDto(payment);
    }

    public List<Payment> getAllPayments(){
        return paymentRepo.findAll();
    }

    public String processPayment(PaymentMode type, BigDecimal amount) {
        processors.get(type.name()).pay(amount);
        return "Payment done via: " + type;
    }

    public void updatePaymentStatus(PaymentResultDto paymentResultDto) {
            paymentRepo.updatePaymentStatus(paymentResultDto.getPaymentId(), paymentResultDto.getPaymentStatus());//TODO: check if entry exists or not
            //call orderService with respectiveEvent
            //for now doing directly
            paymentProducer.sendOrder(paymentResultDto);
            orderService.updateStatus(paymentResultDto);

    }

}
