package org.example.miniordermanagement.Service;

import org.example.miniordermanagement.dto.PlaceOrderRequest;
import org.example.miniordermanagement.dto.PlaceOrderResponse;
import org.example.miniordermanagement.dto.ProductDto;
import org.example.miniordermanagement.enums.OrderStatus;
import org.example.miniordermanagement.enums.PaymentStatus;
import org.example.miniordermanagement.models.Customer;
import org.example.miniordermanagement.models.Orders;
import org.example.miniordermanagement.models.Payment;
import org.example.miniordermanagement.models.Product;
import org.example.miniordermanagement.repository.CustomerRepo;
import org.example.miniordermanagement.repository.OrderRepo;
import org.example.miniordermanagement.repository.PaymentRepo;
import org.example.miniordermanagement.repository.ProductRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private CustomerRepo customerRepo;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldPlaceOrderSuccessfully() {
        // given
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setCustomerId(1L);

        ProductDto productDto = new ProductDto();
        productDto.setId(10L);
        productDto.setStockQuantity(2);
        productDto.setPrice(BigDecimal.valueOf(100));

        request.setProducts(List.of(productDto));
        Customer customer = Customer.builder().id(1L).build();

        Product product = Product.builder()
                .id(10L)
                .price(BigDecimal.valueOf(100))
                .stockQuantity(10)
                .build();

        when(customerRepo.getReferenceById(1L)).thenReturn(customer);
        when(productRepo.findById(10L)).thenReturn(Optional.of(product));

        // when
        PlaceOrderResponse response = orderService.placeOrder(request);

        // then
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getProducts()).hasSize(1);

        verify(orderRepo).save(any(Orders.class));
        verify(paymentRepo).save(any(Payment.class));
    }

    @Test
    void placeOrder_stockInsufficient_throwsException() {
        Product product = new Product();
        product.setId(10L);
        product.setPrice(new BigDecimal("100"));
        product.setStockQuantity(1);

        ProductDto productDto = new ProductDto();
        productDto.setId(10L);
        productDto.setStockQuantity(2); // more than available
        productDto.setPrice(new BigDecimal("100"));

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setCustomerId(1L);
        request.setProducts(List.of(productDto));

        Customer customer = new Customer();
        customer.setId(1L);
        when(customerRepo.getReferenceById(1L)).thenReturn(customer);
        when(productRepo.findById(10L)).thenReturn(Optional.of(product));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.placeOrder(request));
        assertThat(ex.getMessage()).contains("Stock quantity less than stock quantity: 2");

        verify(orderRepo, never()).save(any());
        verify(paymentRepo, never()).save(any());
    }

    @Test
    void placeOrder_productNotFound_throwsException() {
        ProductDto productDto = new ProductDto();
        productDto.setId(99L);
        productDto.setStockQuantity(1);
        productDto.setPrice(new BigDecimal("50"));

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setCustomerId(1L);
        request.setProducts(List.of(productDto));

        Customer customer = new Customer();
        customer.setId(1L);
        when(customerRepo.getReferenceById(1L)).thenReturn(customer);
        when(productRepo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.placeOrder(request));
        assertThat(ex.getMessage()).contains("Product not found productId: 99");

        verify(orderRepo, never()).save(any());
        verify(paymentRepo, never()).save(any());
    }

    @Test
    void placeOrder_success() {
        // Arrange
        Customer customer = new Customer();
        customer.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setPrice(new BigDecimal("100"));
        product.setStockQuantity(5);

        ProductDto productDto = new ProductDto();
        productDto.setId(10L);
        productDto.setStockQuantity(2);
        productDto.setPrice(new BigDecimal("100"));

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setCustomerId(1L);
        request.setProducts(List.of(productDto));

        when(customerRepo.getReferenceById(1L)).thenReturn(customer);
        when(productRepo.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepo.save(any(Orders.class))).thenAnswer(i -> i.getArgument(0));
        when(paymentRepo.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        PlaceOrderResponse response = orderService.placeOrder(request);

        // Assert response
        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getProducts()).hasSize(1);

        // Capture saved order and payment
        ArgumentCaptor<Orders> orderCaptor = ArgumentCaptor.forClass(Orders.class);
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        verify(orderRepo).save(orderCaptor.capture());
        verify(paymentRepo).save(paymentCaptor.capture());

        Orders savedOrder = orderCaptor.getValue();
        Payment savedPayment = paymentCaptor.getValue();

        assertThat(savedOrder.getItems()).hasSize(1);
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("200")); // 2 * 100
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(savedPayment.getAmount()).isEqualByComparingTo(new BigDecimal("200"));
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);

        // Stock should be reduced
        assertThat(product.getStockQuantity()).isEqualTo(3);
    }


}
