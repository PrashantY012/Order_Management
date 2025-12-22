package org.example.miniordermanagement.Service;
import jakarta.transaction.Transactional;
import org.example.miniordermanagement.dto.PlaceOrderRequest;
import org.example.miniordermanagement.dto.PlaceOrderResponse;
import org.example.miniordermanagement.dto.ProductDto;
import org.example.miniordermanagement.dto.UpdateStatus;
import org.example.miniordermanagement.enums.OrderStatus;
import org.example.miniordermanagement.enums.PaymentStatus;
import org.example.miniordermanagement.models.*;
import org.example.miniordermanagement.repository.CustomerRepo;
import org.example.miniordermanagement.repository.OrderRepo;
import org.example.miniordermanagement.repository.PaymentRepo;
import org.example.miniordermanagement.repository.ProductRepo;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {
    private final OrderRepo orderRepo;
    private final PaymentRepo paymentRepo;
    private final ProductRepo productRepo;
    private final CustomerRepo customerRepo;

    public OrderService(OrderRepo orderRepo, PaymentRepo paymentRepo, ProductRepo productRepo, CustomerRepo customerRepo) {
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
    }


    @Transactional //TODO: not atomic
    public PlaceOrderResponse placeOrder(PlaceOrderRequest placeOrderRequest){
        //check all items are available or not
        BigDecimal totalPrice = BigDecimal.ZERO;
        //create a orderNow
        Customer customer = customerRepo.getReferenceById(placeOrderRequest.getCustomerId());
        Orders orders = Orders.builder().totalAmount(totalPrice).status(OrderStatus.PENDING).customer(customer).build();

        for(ProductDto productDto : placeOrderRequest.getProducts()){
                Product product = productRepo.findById(productDto.getId()).orElse(null);
                if(product == null){
                    throw new RuntimeException("Product not found productId: "+productDto.getId());
                }

                if(product.getStockQuantity() < productDto.getStockQuantity()){
                    throw new RuntimeException("Stock quantity less than stock quantity: "+productDto.getStockQuantity());
                }

                OrderItem orderItem = OrderItem.builder().quantity(productDto.getStockQuantity()).individualPrice(product.getPrice()).totalPrice(productDto.getPrice().multiply(BigDecimal.valueOf(productDto.getStockQuantity()))).order(orders).product(product).build();
                product.setStockQuantity(product.getStockQuantity()-productDto.getStockQuantity());
                totalPrice = totalPrice.add(productDto.getPrice().multiply(BigDecimal.valueOf(productDto.getStockQuantity()))  );
//                productRepo.save(product);
                orders.getItems().add(orderItem);
        }

        orders.setTotalAmount(totalPrice);
        Payment payment = Payment.builder().amount(totalPrice).status(PaymentStatus.PENDING).order(orders).build();
        payment.setStatus(PaymentStatus.PENDING);
        orders.setStatus(OrderStatus.PENDING);


        orderRepo.save(orders);
        paymentRepo.save(payment);
        PlaceOrderResponse placeOrderResponse = PlaceOrderResponse.builder().orderId(orders.getId()).paymentId(payment.getId()).build();
        placeOrderResponse.setCustomerId(customer.getId());
        placeOrderResponse.setProducts(placeOrderRequest.getProducts());
        return placeOrderResponse;
    }

    public Boolean updateStatus(UpdateStatus updateStatus){
        Orders orders = orderRepo.findById(updateStatus.getOrder_id()).orElse(null);
        if(orders == null){
            throw new RuntimeException("Order not found to update status: "+updateStatus.getOrder_id());
        }
        OrderStatus orderStatus = OrderStatus.valueOf(String.valueOf(updateStatus.getPaymentStatus()));
        orders.setStatus(orderStatus);

        orderRepo.save(orders);
        return true;
    }

    public List<Orders> getAllOrders(){
        return orderRepo.findAll();
    }


}
