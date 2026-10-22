package com.expensesplitter.controller;

import com.expensesplitter.dto.ExpenseRequest;
import com.expensesplitter.entity.Expense;
import com.expensesplitter.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // POST /api/groups/1/expenses -> 201 Created, or 400 if the split is invalid
    @PostMapping("/groups/{groupId}/expenses")
    public ResponseEntity<Expense> addExpense(@PathVariable Long groupId, @RequestBody ExpenseRequest request) {
        return new ResponseEntity<>(expenseService.addExpense(groupId, request), HttpStatus.CREATED);
    }

    // GET /api/groups/1/expenses            -> all expenses
    // GET /api/groups/1/expenses?userId=2   -> only expenses involving user 2
    @GetMapping("/groups/{groupId}/expenses")
    public List<Expense> getExpenses(@PathVariable Long groupId,
                                     @RequestParam(required = false) Long userId) {
        return expenseService.getExpenses(groupId, userId);
    }

    // DELETE /api/expenses/5 -> 204 No Content
    @DeleteMapping("/expenses/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }
}
