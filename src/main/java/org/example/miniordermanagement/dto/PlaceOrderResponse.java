package org.example.miniordermanagement.dto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceOrderResponse extends PlaceOrderRequest{
    Long paymentId;
    Long orderId;
    @Builder.Default
    List<String> productIds = new ArrayList<>();

}


