package org.example.miniordermanagement.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;

}
