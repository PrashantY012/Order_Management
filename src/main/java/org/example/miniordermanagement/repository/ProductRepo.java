package org.example.miniordermanagement.repository;
import org.example.miniordermanagement.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProductRepo extends JpaRepository<Product,Long> {


        @Modifying
        @Query("""
        UPDATE Product p
        SET p.stockQuantity = p.stockQuantity - :qty
        WHERE p.id = :id AND p.stockQuantity >= :qty
    """)
        Long subtractIfEnough(@Param("id") Long id,
                             @Param("qty") int qty);



        @Modifying
        @Query("""
        UPDATE Product p
        SET p.stockQuantity = p.stockQuantity +: qty
        WHERE p.id = :id
""")
    Integer addStock(@Param("id") Long id,
                        @Param ("qty") int qty);









}
