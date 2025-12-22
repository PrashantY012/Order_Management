package org.example.miniordermanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.miniordermanagement.Service.CustomerService;
import org.example.miniordermanagement.dto.CustomerDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/customer")
@Tag(name = "Customer", description = "Customer management APIs")
public class CustomerController {
    private final CustomerService customerService;
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Register a customer by providing their name and email")
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerDto customerDto){
        String res = customerService.registerCustomer(customerDto);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/getInfo/{email}")
    public ResponseEntity<?> getUser(@PathVariable String email){
        CustomerDto res = customerService.getCustomer(email);
        return ResponseEntity.ok().body(res);
    }
    

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String email){
        String res = customerService.deleteCustomer(email);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/triggerException")
    public ResponseEntity<?> getCustomers(){
        throw new RuntimeException("Throwing new runtime exception");
    }

}
