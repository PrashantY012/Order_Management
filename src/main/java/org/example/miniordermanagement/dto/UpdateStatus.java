package org.example.miniordermanagement.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.miniordermanagement.enums.PaymentStatus;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatus {
    Integer order_id;
    Integer payement_id;
    PaymentStatus paymentStatus;
}
