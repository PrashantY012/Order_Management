package org.example.miniordermanagement.repository;

import org.example.miniordermanagement.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Orders, Integer> {
}
