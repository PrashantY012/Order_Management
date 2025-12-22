package org.example.miniordermanagement.repository;

import org.example.miniordermanagement.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo  extends JpaRepository<Customer,Long> {

    Customer findByEmail(String email);

    Long deleteByEmail(String email);
}
