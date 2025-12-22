package org.example.miniordermanagement.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @Column(name="individual_price")
    private BigDecimal individualPrice;

    @Column(name="total_price")
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="product_id")
    private Product product;
}

