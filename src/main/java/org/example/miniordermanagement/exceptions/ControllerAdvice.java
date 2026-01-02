package org.example.miniordermanagement.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value={RuntimeException.class})
    public ResponseEntity<?> handleException(RuntimeException ex){
        System.out.println("Exception caught in ControllerAdvice: "+ex);
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());

    }

    @ExceptionHandler(value={LockNotAvailableException.class})
    public ResponseEntity<?> handleLockNotAvailableException(RuntimeException ex){
        System.out.println("Exception caught in ControllerAdvice: "+ex);
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "CartLocked",
                "message", ex.getMessage()
        ));
    }

}
