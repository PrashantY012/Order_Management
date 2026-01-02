package org.example.miniordermanagement.controller;
import org.example.miniordermanagement.service.ProductService;
import org.example.miniordermanagement.dto.ProductDto;
import org.example.miniordermanagement.models.Product;
import org.example.miniordermanagement.repository.ProductRepo;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;


@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final ProductRepo productRepo;

    ProductController(ProductService productService, ProductRepo productRepo){
        this.productService = productService;
        this.productRepo = productRepo;
    }

    @PostMapping("")
    public ResponseEntity<?> addProduct(@RequestBody ProductDto productDto){
        String res =   productService.addProduct(productDto);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> addProductInBatch(@RequestBody List<ProductDto> productDtos){
        List<String> res =   productService.addProduct(productDtos);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/v1")
    public ResponseEntity<?> addStock(@RequestBody ProductDto productDto){
        String res =   productService.addStock(productDto);
        return ResponseEntity.ok().body(res);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<?> removeStock(@RequestBody ProductDto productDto, @PathVariable String productId){
        productDto.setId(Long.valueOf(productId));
        String res = productService.reduceStock(productDto);
        return ResponseEntity.ok().body(res);
    }


    @GetMapping()
    public Page<Product> getOrders(Pageable pageable) {
        return productRepo.findAll(pageable);
    }


}
