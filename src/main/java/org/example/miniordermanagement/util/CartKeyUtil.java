package org.example.miniordermanagement.util;
// CartKeyUtil.java
public class CartKeyUtil {

    private CartKeyUtil() {}

    public static String cartKey(String userId) {
        return "cart:" + userId;
    }
}

