package org.example.miniordermanagement.controller;
import org.example.miniordermanagement.Service.PaymentService;
import org.example.miniordermanagement.dto.PaymentDto;
import org.example.miniordermanagement.dto.PaymentResultDto;
import org.example.miniordermanagement.models.Payment;
import org.example.miniordermanagement.repository.PaymentRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/updateStatus")
    public ResponseEntity<?> savePayment(@RequestBody PaymentDto paymentDto) {

            PaymentDto res = paymentService.updatePaymentStatus(paymentDto);
            return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllPayment() {
        List<Payment> res = paymentService.getAllPayments();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody PaymentDto paymentDto) {
        String res = paymentService.processPayment(paymentDto.getPaymentMode(), paymentDto.getAmount());
        return ResponseEntity.ok(res);
    }


    @PostMapping("/webhook")
    public ResponseEntity<?> paymentStatus(@RequestBody PaymentResultDto paymentResultDto) {
                paymentService.updatePaymentStatus(paymentResultDto);
                return ResponseEntity.noContent().build();
    }


}
