package org.example.miniordermanagement.models;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.Instant;


@Entity
@Getter
@Setter
@Table(name="product")
@Builder
@AllArgsConstructor
public class Product {

    @Id
    private Long id;

    private String name;

    private BigDecimal price;

    @Column(name="stock_quantity")
    private Integer stockQuantity;

    @CreationTimestamp
    @Column(name="updated_at")
    private Instant updatedAt;


    Product(Long id, String name, BigDecimal price, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Product() {

    }
}

