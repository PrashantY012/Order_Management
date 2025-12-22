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
import org.example.miniordermanagement.util.RedisKeyUtil;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderService {
    private final OrderRepo orderRepo;
    private final PaymentRepo paymentRepo;
    private final ProductRepo productRepo;
    private final CustomerRepo customerRepo;
    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOps;
    private final DefaultRedisScript<Long> lockStockScript;

    public OrderService(OrderRepo orderRepo, PaymentRepo paymentRepo, ProductRepo productRepo, CustomerRepo customerRepo, RedisTemplate<String, String> redisTemplate, HashOperations<String, String, String> hashOps, DefaultRedisScript<Long> lockStockScript) {
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.redisTemplate = redisTemplate;
        this.hashOps = hashOps;
        this.lockStockScript = lockStockScript;
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


    @Transactional //TODO: not atomic
    public PlaceOrderResponse placeOrderViaCart(String userId){
        //check all items are available or not
        BigDecimal totalPrice = BigDecimal.ZERO;
        //create a orderNow
        Map<String, String> entries =
                hashOps.entries(RedisKeyUtil.cartKey(userId));
        Customer customer = customerRepo.getReferenceById(Long.valueOf(userId));
        Orders orders = Orders.builder().totalAmount(totalPrice).status(OrderStatus.PENDING).customer(customer).build();

        for(Map.Entry<String,String>prod : entries.entrySet()){
            Long prodId = Long.valueOf(prod.getKey());
            Integer reqQty = Integer.valueOf(prod.getValue());
            Product product = productRepo.findById(Long.valueOf(prod.getKey())).orElse(null);
            if(product == null){
                throw new RuntimeException("Product not found productId: "+ prod.getKey());
            }

            if(product.getStockQuantity() < reqQty){
                throw new RuntimeException("Stock quantity less than stock quantity: "+reqQty);
            }

            OrderItem orderItem = OrderItem.builder().quantity(reqQty).individualPrice(product.getPrice()).totalPrice(product.getPrice().multiply(BigDecimal.valueOf(reqQty))).order(orders).product(product).build();
            product.setStockQuantity(product.getStockQuantity()-reqQty);
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(reqQty)));
            orders.getItems().add(orderItem);
        }

        orders.setTotalAmount(totalPrice);
        Payment payment = Payment.builder().amount(totalPrice).status(PaymentStatus.PENDING).order(orders).build();
        payment.setStatus(PaymentStatus.PENDING);
        orders.setStatus(OrderStatus.PENDING);

        orderRepo.save(orders);
        paymentRepo.save(payment);
        PlaceOrderResponse placeOrderResponse = PlaceOrderResponse.builder().orderId(orders.getId()).paymentId(payment.getId()).build();
        placeOrderResponse.setProductIds(entries.keySet().stream().toList());
        placeOrderResponse.setCustomerId(customer.getId());
        return placeOrderResponse;
    }

    /*
       @param items map of productId and their stock as needed by the user on checkout
     */
    public boolean lockStock(String orderId, Map<String, String> items) {

        List<String> stockKeys = items.keySet().stream()
                .map(p -> RedisKeyUtil.getProductKey(p) )
                .toList();

        List<String> args = new ArrayList<>();
        items.values().forEach(q -> args.add(q));
        args.add(orderId);
        args.add("600"); // 10 min lock TTL

        Long result = redisTemplate.execute(
                lockStockScript,
                stockKeys,
                args.toArray()
        );

        return result != null && result == 1;
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
