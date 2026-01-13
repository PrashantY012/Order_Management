package org.example.miniordermanagement.service;
import org.example.miniordermanagement.dto.CartItem;
import org.example.miniordermanagement.util.RedisKeyUtil;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.Map;


// CartService.java
@Service
public class CartService {

    private static final Duration CART_TTL = Duration.ofHours(24);

    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOps;
    private final OrderService orderService;

    public CartService(RedisTemplate<String, String> redisTemplate, OrderService orderService) {
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
        this.orderService = orderService;
    }

    public void addItem(String userId, String productId, int quantity) {
        String key = RedisKeyUtil.cartKey(userId);
        hashOps.put(key, productId, String.valueOf(quantity));
        redisTemplate.expire(key, CART_TTL);
    }

    public void updateItem(String userId, String productId, int quantity) {
        String key = RedisKeyUtil.cartKey(userId);

        if (quantity <= 0) {
            hashOps.delete(key, productId);
        } else {
            hashOps.put(key, productId, String.valueOf(quantity));
            redisTemplate.expire(key, CART_TTL);
        }
    }

    public void removeItem(String userId, String productId) {
        hashOps.delete(RedisKeyUtil.cartKey(userId), productId);
    }

    /*
            If cart is locked, unlock it, reset the entries that were there in cache.
            Pending status orders will themselves become expired after cron, new pg page, new lock.
     */
    public List<CartItem> getCart(String userId) {
        Map<String, String> entries =
                hashOps.entries(RedisKeyUtil.cartKey(userId));

        List<CartItem> cartItemList = entries.entrySet()
                .stream()
                .map(e -> new CartItem(
                        e.getKey(),
                        Integer.parseInt(e.getValue())))
                .toList();

        orderService.releaseStock(entries, userId);
        return cartItemList;

    }

    public void clearCart(String userId) {
        redisTemplate.delete(RedisKeyUtil.cartKey(userId));
    }


    public boolean isCartEmpty(String userId) {
        return redisTemplate.opsForHash()
                .size(RedisKeyUtil.cartKey(userId)) == 0;
    }
}

