package org.example.miniordermanagement.Service;
import jakarta.transaction.Transactional;
import org.example.miniordermanagement.Service.processor.PaymentProcessor;
import org.example.miniordermanagement.dto.ProductDto;
import org.example.miniordermanagement.models.Product;
import org.example.miniordermanagement.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.Map;


@Transactional
@Service
public class ProductService {
    private final ProductRepo productRepo;
    public Map<String, PaymentProcessor> processors;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }
//    public void addProduct(Prod)

    Product getProductFromProductDto(ProductDto productDto){
        return Product.builder().id(productDto.getId()).price(productDto.getPrice()).stockQuantity(productDto.getStockQuantity()).build();
    }

    ProductDto getProductDtoFromProduct(Product product){
        return ProductDto.builder().id(product.getId()).price(product.getPrice()).stockQuantity(product.getStockQuantity()).build();
    }

    public String addProduct(ProductDto productDto){
        Product product = getProductFromProductDto(productDto);
        product = productRepo.save(product);
        return product == null ? "could not add product for id "+product.getId() : "Product added successfully for id "+product.getId();
    }


    public String addStock(ProductDto productDto){
//        Product product = getProductFromProductDto(productDto); //TODO: throw errors
        Integer count = productRepo.addStock(productDto.getId(), productDto.getStockQuantity());
        return count == 0 ? "No product found for id "+productDto.getId() : "Stock updated successfully for product found for id "+productDto.getId();
    }


    public String updateStock(ProductDto productDto){
        Product product = getProductFromProductDto(productDto); //TODO: throw errors
        Product updated = productRepo.save(product);
        return "Product added succesfully";
    }


    public String reduceStock(ProductDto productDto){
//      Product product = getProductFromProductDto(productDto); //TODO throw errors
        productRepo.subtractIfEnough(productDto.getId(), productDto.getStockQuantity()); //TODO: update
        return "Product removed successfully";
    }




}
