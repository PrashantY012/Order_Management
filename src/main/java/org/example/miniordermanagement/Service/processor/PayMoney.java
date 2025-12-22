package org.example.miniordermanagement.Service.processor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PayMoney {
    public Map<String,PaymentProcessor> processors;

    public PayMoney(Map<String,PaymentProcessor> processors) {
        this.processors = processors;
    }


}
