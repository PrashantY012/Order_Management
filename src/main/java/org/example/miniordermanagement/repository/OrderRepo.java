package org.example.miniordermanagement.repository;
import jakarta.persistence.LockModeType;
import org.example.miniordermanagement.enums.OrderStatus;
import org.example.miniordermanagement.enums.PaymentStatus;
import org.example.miniordermanagement.models.Orders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Orders, Integer> {
    @Modifying
    @Transactional
    @Query("""
        UPDATE Orders o
        SET o.status = :status
        WHERE o.id = :orderId
    """)
    int updateOrderStatus(
            @Param("orderId") String orderId,
            @Param("status") OrderStatus status
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT o FROM Orders o
        WHERE o.status = :status
          AND o.id IN (
              SELECT p.order.id FROM Payment p
              WHERE p.createdAt < :expiryTime
                AND p.status = :paymentStatus
          )
    """)
    List<Orders> findExpiredPendingOrders(
            @Param("status") OrderStatus orderStatus,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("expiryTime") Instant expiryTime,
            Pageable pageable
    );

    @Transactional(readOnly = true)
    @Query(""" 
                            select o.customer.id from Orders o
                            where id =:id
                            """)
    Optional<String> getUserIdFromOrderId(
            @Param("id") Long id
    );
}
