package org.example.miniordermanagement.repository;

import org.example.miniordermanagement.enums.PaymentStatus;
import org.example.miniordermanagement.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    @Modifying
    @Transactional
    @Query("""
        UPDATE Payment p
        SET p.status = :status
        WHERE p.id = :paymentId
    """)
    int updatePaymentStatus(
            @Param("paymentId") String paymentId,
            @Param("status") PaymentStatus status
    );

    @Query("""
        SELECT p.order.id
        FROM Payment p
        WHERE p.id = :paymentId
    """)
    Long findOrderIdByPaymentId(@Param("paymentId") Long paymentId);

    Optional<Payment> findByOrderId(Long orderId);

}
