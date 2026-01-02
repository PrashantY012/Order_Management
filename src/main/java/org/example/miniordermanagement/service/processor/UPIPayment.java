package org.example.miniordermanagement.service.processor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service("UPI")
public class UPIPayment implements PaymentProcessor {
    @Override
    public void pay(BigDecimal amount) {
        System.out.println("UPIPayment pay amount: " + amount);
    }
}
