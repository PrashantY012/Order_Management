package org.example.miniordermanagement.service;
import jakarta.transaction.Transactional;
import org.example.miniordermanagement.dto.*;
import org.example.miniordermanagement.enums.OrderStatus;
import org.example.miniordermanagement.enums.PaymentStatus;
import org.example.miniordermanagement.exceptions.LockNotAvailableException;
import org.example.miniordermanagement.models.*;
import org.example.miniordermanagement.repository.CustomerRepo;
import org.example.miniordermanagement.repository.OrderRepo;
import org.example.miniordermanagement.repository.PaymentRepo;
import org.example.miniordermanagement.repository.ProductRepo;
import org.example.miniordermanagement.util.PaymentToOrderStatusMapper;
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
    private final DefaultRedisScript<Long> releaseStockScript;
    private final DefaultRedisScript<Long> commitStockScript;
    private final DefaultRedisScript<Long> reserveStockScript;
    private final PaymentToOrderStatusMapper paymentToOrderStatusMapper;

    public OrderService(OrderRepo orderRepo, PaymentRepo paymentRepo, ProductRepo productRepo, CustomerRepo customerRepo, RedisTemplate<String, String> redisTemplate, DefaultRedisScript<Long> lockStockScript, DefaultRedisScript<Long> releaseStockScript, DefaultRedisScript<Long> commitStockScript, DefaultRedisScript<Long> reserveStockScript, PaymentToOrderStatusMapper paymentToOrderStatusMapper) {
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
        this.lockStockScript = lockStockScript;
        this.releaseStockScript = releaseStockScript;
        this.commitStockScript = commitStockScript;
        this.reserveStockScript = reserveStockScript;
        this.paymentToOrderStatusMapper = paymentToOrderStatusMapper;
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
        paymentRepo.save(payment); //TODO: can do better, its in order repo...
        PlaceOrderResponse placeOrderResponse = PlaceOrderResponse.builder().orderId(orders.getId()).paymentId(payment.getId()).build();
        placeOrderResponse.setProductIds(entries.keySet().stream().toList());
        placeOrderResponse.setCustomerId(customer.getId());
        boolean reservedStock = reserveStock(String.valueOf(orders.getId()), entries, null);
        if(reservedStock == false){
            throw new RuntimeException("Could not reserve stock in redis");
        }
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


    public void updateStatus(PaymentResultDto paymentResultDto){
        OrderStatus orderStatus = paymentToOrderStatusMapper.map(paymentResultDto.getPaymentStatus());
        Long orderId = paymentRepo.findOrderIdByPaymentId(Long.valueOf(paymentResultDto.getPaymentId()));
//        orderRepo.updateOrderStatus(String.valueOf(orderId), orderStatus);
        String userId = orderRepo.getUserIdFromOrderId(orderId).orElse(null);
        //depending on the status run the respective lua
        Map<String, String> entries =
                hashOps.entries(RedisKeyUtil.cartKey(paymentResultDto.getUserId()));
        if(orderStatus == OrderStatus.CANCELLED){
                releaseStock(String.valueOf(orderId), entries, userId);
        } else if(orderStatus == OrderStatus.SUCCESS){
            boolean done = commitStock(String.valueOf(orderId), entries, userId);
            if(done){
                handlePaymentSuccess(Math.toIntExact(orderId));
            }
        }
    }

    @Transactional
    public void handlePaymentSuccess(Integer orderId) {

        Orders order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found for orderId: "+orderId));

        // Idempotency check
        if (order.getStatus() == OrderStatus.SUCCESS) {
            return;
        }

        // Reduce stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: "+product.getId());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepo.save(product);
        }

        order.setStatus(OrderStatus.SUCCESS);
        orderRepo.save(order);
    }



    public boolean releaseStock(String orderId, String userId){
        Map<String, String> entries =
                hashOps.entries(RedisKeyUtil.cartKey(userId));
        return releaseStock(orderId, entries, userId );
    }

    /*
       @param items map of productId and their stock as needed by the user on checkout
     */
    public boolean reserveStock(String orderId, Map<String, String> items, String userId) {

        List<String> stockKeys = items.keySet().stream()
                .map(p -> RedisKeyUtil.getProductKey(p) )
                .toList();

        List<String> args = new ArrayList<>();
        items.values().forEach(q -> args.add(q));
        args.add(userId);
        args.add(orderId);
        args.add("600"); // 10 min lock TTL

        Long result = redisTemplate.execute(
                reserveStockScript,
                stockKeys,
                args.toArray()
        );

        if(result == -2){
            throw new LockNotAvailableException("Cart is already locked for user:"+userId);
        }
        System.out.println("result: " + result);
        return result != null && result == 1;
    }


    public boolean releaseStock(String orderId, Map<String, String> items, String userId) {
        List<String> stockKeys = items.keySet().stream()
                .map(p -> RedisKeyUtil.getProductKey(p) )
                .toList();

        List<String> args = new ArrayList<>();
        items.values().forEach(q -> args.add(q));
        args.add(orderId);
        args.add(userId);
//        args.add("600"); // 10 min lock TTL

        Long result = redisTemplate.execute(
                releaseStockScript,
                stockKeys,
                args.toArray()
        );
        System.out.println("result: " + result);
        return result != null && result == 1;
    }


    public boolean commitStock(String orderId, Map<String, String> items, String userId) {
        List<String> stockKeys = items.keySet().stream()
                .map(p -> RedisKeyUtil.getProductKey(p) )
                .toList();

        List<String> args = new ArrayList<>();
        items.values().forEach(q -> args.add(q));
        args.add(orderId);
        args.add(userId);
//        args.add("600"); // 10 min lock TTL

        Long result = redisTemplate.execute(
                commitStockScript,
                stockKeys,
                args.toArray()
        );
        System.out.println("result: " + result);
        return result != null && result == 1;
    }


}
