package org.example.miniordermanagement.service.processor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("NETBANKING")
public class NetBankingProcessor implements PaymentProcessor {
    @Override
    public void pay(BigDecimal amount) {
            System.out.println("NetBankingProcessor pay amount: " + amount);
    }
}
