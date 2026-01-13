package org.example.miniordermanagement.controller;

import org.example.miniordermanagement.service.OrderService;
import org.example.miniordermanagement.dto.PlaceOrderRequest;
import org.example.miniordermanagement.dto.PlaceOrderResponse;
import org.example.miniordermanagement.dto.UpdateStatus;
import org.example.miniordermanagement.models.Orders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/v1")
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest placeOrderRequest){
        PlaceOrderResponse reponse = orderService.placeOrder(placeOrderRequest);
        return new ResponseEntity<>(reponse, HttpStatus.OK);
    }

    /*
        Called when user clicks on proceed to pay,
        Makes entry in Order and Payment Table with pending status.
     */
    @PostMapping("/v2")
    public ResponseEntity<?> placeOrderViaCart(@RequestBody PlaceOrderRequest request){
           PlaceOrderResponse res = orderService.placeOrderViaCart(String.valueOf(request.getCustomerId()));
           return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    @GetMapping()
    public ResponseEntity<?> getAllOrder(){
        List<Orders> res = orderService.getAllOrders();
        return ResponseEntity.ok().body(res);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(@RequestBody UpdateStatus updateStatus, @PathVariable String orderId){
        updateStatus.setOrder_id(Integer.valueOf(orderId));
        Boolean res = orderService.updateStatus(updateStatus);
        return ResponseEntity.ok().body(res);

    }
}
