package org.example.miniordermanagement.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CalculatorTest {

    @InjectMocks
    private Calculator calc;

    @Test
    void basic_sum_test(){
        int result = calc.getSum(1, 2);
        int expected_result = 3;
        assertEquals(expected_result, result);
    }
}