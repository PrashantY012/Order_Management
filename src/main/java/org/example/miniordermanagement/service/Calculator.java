package org.example.miniordermanagement.service;
import org.springframework.stereotype.Service;

@Service
public class Calculator {
    public int getSum(int a, int b){
        return a+b;
    }
}
