package org.example.miniordermanagement.dto;
import lombok.*;
import org.example.miniordermanagement.enums.PaymentMode;
import org.example.miniordermanagement.enums.PaymentStatus;
import org.example.miniordermanagement.models.Orders;
import org.example.miniordermanagement.models.Payment;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {
    private Long id;
    private PaymentStatus status;
    private BigDecimal amount;
    private Orders orders;
    private PaymentMode paymentMode;

    public PaymentDto(Payment payment) {
        this.id = payment.getId();
        this.status = payment.getStatus();
        this.amount = payment.getAmount();
        this.orders = payment.getOrder();
    }
}
