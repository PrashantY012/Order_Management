package org.example.miniordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.miniordermanagement.enums.PaymentStatus;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultDto {
    private String paymentId;
    private PaymentStatus paymentStatus;
    private String userId;//TODO: not from here
}
