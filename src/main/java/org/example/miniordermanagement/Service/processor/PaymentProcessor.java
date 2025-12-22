package org.example.miniordermanagement.Service.processor;

import java.math.BigDecimal;

public interface PaymentProcessor {
    void pay(BigDecimal amount);
}
