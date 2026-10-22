package com.expensesplitter.controller;

import com.expensesplitter.dto.PaymentRequest;
import com.expensesplitter.entity.Payment;
import com.expensesplitter.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // POST /api/groups/1/payments  body: {"fromUserId": 4, "toUserId": 1, "amount": 500} -> 201 Created
    @PostMapping("/groups/{groupId}/payments")
    public ResponseEntity<Payment> recordPayment(@PathVariable Long groupId, @RequestBody PaymentRequest request) {
        return new ResponseEntity<>(paymentService.recordPayment(groupId, request), HttpStatus.CREATED);
    }

    // GET /api/groups/1/payments -> payments recorded so far (newest first)
    @GetMapping("/groups/{groupId}/payments")
    public List<Payment> getPayments(@PathVariable Long groupId) {
        return paymentService.getPayments(groupId);
    }

    // DELETE /api/payments/3 -> 204 No Content (undo a payment)
    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.noContent().build();
    }
}
