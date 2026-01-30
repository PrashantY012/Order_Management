package org.example.miniordermanagement.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "customer")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String password;

    @Column(name="create_at")
    @CreationTimestamp
    private Instant createdAt;

    public Customer() {}

    public Customer(String name, String email){
        this.name = name;
        this.email = email;
    }

}
