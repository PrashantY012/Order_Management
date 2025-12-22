package org.example.miniordermanagement.util;
// CartKeyUtil.java
public class RedisKeyUtil {

    private RedisKeyUtil() {}

    public static String cartKey(String userId) {
        return "cart:" + userId;
    }

    public static String getProductKey(String productId) {
        return "product:" + productId;
    }

    public static String getStockKey() {
        return "stock:";
    }
}

