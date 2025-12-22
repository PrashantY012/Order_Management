package org.example.miniordermanagement.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    String productId;
    Integer quantity;
    String userId;

    public CartItem(String productId, Integer quantity){
        this.productId = productId;
        this.quantity = quantity;
    }

}
