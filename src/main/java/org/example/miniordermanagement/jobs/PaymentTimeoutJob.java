package org.example.miniordermanagement.jobs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.miniordermanagement.Service.OrderService;
import org.example.miniordermanagement.enums.OrderStatus;
import org.example.miniordermanagement.enums.PaymentStatus;
import org.example.miniordermanagement.models.Orders;
import org.example.miniordermanagement.repository.OrderRepo;
import org.example.miniordermanagement.repository.PaymentRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentTimeoutJob {

    private final OrderRepo orderRepo;
    private final PaymentRepo paymentRepository;
    private final OrderService orderService;

    private static final Duration PAYMENT_TIMEOUT = Duration.ofMinutes(15);
    private static final int BATCH_SIZE = 50;

    @Scheduled(fixedRate = 1000*5) // every 1 minute
    @Transactional
    public void handlePaymentTimeouts() {
        System.out.println("DarkDuel cron is running");
        Instant expiryTime = Instant.now().minus(PAYMENT_TIMEOUT);

        List<Orders> expiredOrders =
                orderRepo.findExpiredPendingOrders(
                        OrderStatus.PENDING,
                        PaymentStatus.PENDING,
                        expiryTime,
                        PageRequest.of(0, BATCH_SIZE)
                );

        if (expiredOrders.isEmpty()) {
            return;
        }

        for (Orders order : expiredOrders) {
            timeoutOrder(order);
        }
    }

    private void timeoutOrder(Orders order) {

        // Double-check (idempotency guard)
        if (order.getStatus() != OrderStatus.PENDING) {
            return;
        }

        log.info("Timing out order {}", order.getId());

        // 1️⃣ Update order
        order.setStatus(OrderStatus.TIMEOUT);

        // 2️⃣ Update payment
        paymentRepository.findByOrderId(order.getId())
                .ifPresent(payment -> payment.setStatus(PaymentStatus.FAILED));

        // 3️⃣ Release stock
        orderService.releaseStock(order.getId().toString(), String.valueOf(order.getCustomer().getId()));

        // 4️⃣ Restore / unlock cart
//        cartService.restoreCart(order.getCustomer().getId(), order);

        orderRepo.save(order);
    }
}
