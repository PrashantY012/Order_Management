package org.example.miniordermanagement.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderRequest {
    List<ProductDto> products;
    Long customerId;
}
