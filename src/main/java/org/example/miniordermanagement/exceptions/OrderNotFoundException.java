package org.example.miniordermanagement.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String msg) {
        super(msg);
    }
}
