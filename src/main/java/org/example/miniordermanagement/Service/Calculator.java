package org.example.miniordermanagement.Service;
import org.springframework.stereotype.Service;

@Service
public class Calculator {
    public int getSum(int a, int b){
        return a+b;
    }
}
