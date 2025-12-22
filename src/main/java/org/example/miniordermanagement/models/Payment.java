package org.example.miniordermanagement.models;
import jakarta.persistence.*;
import lombok.*;
import org.example.miniordermanagement.enums.PaymentStatus;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="order_id")
    private Orders order;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @CreationTimestamp
    @Column(name="created_at")
    private Instant createdAt;

}

