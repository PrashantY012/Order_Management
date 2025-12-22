package org.example.miniordermanagement.models;

import jakarta.persistence.*;
import lombok.*;
import org.example.miniordermanagement.enums.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount;

    @CreationTimestamp
    private Instant orderDate;

    @ManyToOne
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

}


