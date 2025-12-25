package org.example.miniordermanagement.util;
import org.example.miniordermanagement.enums.OrderStatus;
import org.example.miniordermanagement.enums.PaymentStatus;
import org.springframework.stereotype.Component;


@Component
public class PaymentToOrderStatusMapper {
    public OrderStatus map(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case SUCCESS -> OrderStatus.SUCCESS;
            case FAILED  -> OrderStatus.CANCELLED;
            case PENDING -> OrderStatus.PENDING;
        };
    }
}
