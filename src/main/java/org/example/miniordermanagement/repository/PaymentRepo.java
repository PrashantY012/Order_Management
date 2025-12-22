package org.example.miniordermanagement.repository;

import org.example.miniordermanagement.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
}
