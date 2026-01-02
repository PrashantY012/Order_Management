package org.example.miniordermanagement.service.processor;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PayMoney {
    public Map<String,PaymentProcessor> processors;

    public PayMoney(Map<String,PaymentProcessor> processors) {
        this.processors = processors;
    }


}
