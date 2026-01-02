package org.example.miniordermanagement.exceptions;

public class LockNotAvailableException extends RuntimeException{
    public LockNotAvailableException(String message){
        super(message);
    }
}
