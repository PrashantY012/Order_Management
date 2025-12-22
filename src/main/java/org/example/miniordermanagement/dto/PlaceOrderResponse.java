package org.example.miniordermanagement.dto;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceOrderResponse extends PlaceOrderRequest{
    Long paymentId;
    Long orderId;

}


