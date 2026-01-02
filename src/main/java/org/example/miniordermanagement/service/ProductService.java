package org.example.miniordermanagement.service;
import jakarta.transaction.Transactional;
import org.example.miniordermanagement.service.processor.PaymentProcessor;
import org.example.miniordermanagement.dto.ProductDto;
import org.example.miniordermanagement.models.Product;
import org.example.miniordermanagement.repository.ProductRepo;
import org.example.miniordermanagement.util.RedisKeyUtil;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Transactional
@Service
public class ProductService {
    private final ProductRepo productRepo;
    public Map<String, PaymentProcessor> processors;

    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOps;

    public ProductService(ProductRepo productRepo, RedisTemplate<String, String> redisTemplate) {
        this.productRepo = productRepo;
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
    }

    Product getProductFromProductDto(ProductDto productDto){
        return Product.builder().id(productDto.getId()).price(productDto.getPrice()).stockQuantity(productDto.getStockQuantity()).build();
    }


    ProductDto getProductDtoFromProduct(Product product){
        return ProductDto.builder().id(product.getId()).price(product.getPrice()).stockQuantity(product.getStockQuantity()).build();
    }

    public String addProduct(ProductDto productDto){
        Product product = getProductFromProductDto(productDto);
        product = productRepo.save(product);
        //store in redis
        if(product == null) {
            return "could not add product for id "+product.getId();
        } else {
            hashOps.put(RedisKeyUtil.getProductKey(String.valueOf(product.getId())), RedisKeyUtil.getAvailableStockKey(), String.valueOf(product.getStockQuantity()));
            hashOps.put(RedisKeyUtil.getProductKey(String.valueOf(product.getId())), RedisKeyUtil.getReservedStockKey(), "0");
            return "Product added successfully for id "+product.getId();}
    }


    public List<String> addProduct(List<ProductDto> productDtoList){
        List<String> res = new ArrayList<>();
        for(ProductDto productdto: productDtoList){
            Product product = getProductFromProductDto(productdto);
            res.add(addProduct(productdto));
        }
        return res;
    }


    public String addStock(ProductDto productDto){
//        Product product = getProductFromProductDto(productDto); //TODO: throw errors
        Integer count = productRepo.addStock(productDto.getId(), productDto.getStockQuantity());
        if( count == 0 )
            return "No product found for id: "+productDto.getId();
        else {
            hashOps.increment( RedisKeyUtil.getProductKey(String.valueOf(productDto.getId())), RedisKeyUtil.getStockKey(), productDto.getStockQuantity() );
            hashOps.increment( RedisKeyUtil.getProductKey(String.valueOf(productDto.getId())), RedisKeyUtil.getReservedStockKey(), productDto.getStockQuantity() );
            return "Stock updated successfully for product found for id " + productDto.getId();
        }
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
