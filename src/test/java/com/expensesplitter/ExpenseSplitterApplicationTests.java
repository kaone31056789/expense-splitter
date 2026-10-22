package com.expensesplitter;

import com.expensesplitter.dto.BalanceResponse;
import com.expensesplitter.dto.ExpenseRequest;
import com.expensesplitter.dto.PaymentRequest;
import com.expensesplitter.dto.SettlementResponse;
import com.expensesplitter.dto.SplitRequest;
import com.expensesplitter.entity.Group;
import com.expensesplitter.entity.User;
import com.expensesplitter.exception.InvalidSplitException;
import com.expensesplitter.repository.ExpenseRepository;
import com.expensesplitter.service.BalanceService;
import com.expensesplitter.service.ExpenseService;
import com.expensesplitter.service.GroupService;
import com.expensesplitter.service.PaymentService;
import com.expensesplitter.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs the same scenario as the Postman test sequence and checks the numbers.
 */
@SpringBootTest
class ExpenseSplitterApplicationTests {

    @Autowired private UserService userService;
    @Autowired private GroupService groupService;
    @Autowired private ExpenseService expenseService;
    @Autowired private BalanceService balanceService;
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private PaymentService paymentService;

    @Test
    void fullScenarioGivesExpectedBalancesAndSettlements() {
        User a = userService.createUser(new User("Aman", "aman@test.com"));
        User b = userService.createUser(new User("Bhavya", "bhavya@test.com"));
        User c = userService.createUser(new User("Chirag", "chirag@test.com"));
        User d = userService.createUser(new User("Diya", "diya@test.com"));

        Group g = groupService.createGroup(new Group("Goa Trip"));
        for (User u : List.of(a, b, c, d)) {
            groupService.addMember(g.getId(), u.getId());
        }

        expenseService.addExpense(g.getId(), request("Dinner", 1200, a, "EQUAL",
                split(a, null), split(b, null), split(c, null), split(d, null)));
        expenseService.addExpense(g.getId(), request("Cab", 1000, b, "PERCENTAGE",
                split(a, 25.0), split(b, 25.0), split(c, 30.0), split(d, 20.0)));
        expenseService.addExpense(g.getId(), request("Movie", 1000, c, "EXACT",
                split(a, 250.0), split(b, 300.0), split(c, 200.0), split(d, 250.0)));

        Map<String, Double> balances = balanceService.getBalances(g.getId()).stream()
                .collect(Collectors.toMap(BalanceResponse::getName, BalanceResponse::getBalance));
        assertEquals(400.0, balances.get("Aman"));
        assertEquals(150.0, balances.get("Bhavya"));
        assertEquals(200.0, balances.get("Chirag"));
        assertEquals(-750.0, balances.get("Diya"));

        List<String> settlements = balanceService.getSettlements(g.getId()).stream()
                .map(SettlementResponse::getMessage).collect(Collectors.toList());
        assertEquals(List.of("Diya pays Aman Rs 400.00", "Diya pays Chirag Rs 200.00",
                "Diya pays Bhavya Rs 150.00"), settlements);

        // Invalid split (percent total 90) must throw and leave NO extra expense behind (rollback)
        long before = expenseRepository.count();
        assertThrows(InvalidSplitException.class, () -> expenseService.addExpense(g.getId(),
                request("Bad", 500, a, "PERCENTAGE", split(a, 50.0), split(b, 40.0))));
        assertEquals(before, expenseRepository.count());

        // Settle-up: Diya pays Aman his Rs 400 -> Aman is settled, Diya still owes Rs 350
        paymentService.recordPayment(g.getId(), payment(d, a, 400));
        Map<String, Double> after = balanceService.getBalances(g.getId()).stream()
                .collect(Collectors.toMap(BalanceResponse::getName, BalanceResponse::getBalance));
        assertEquals(0.0, after.get("Aman"));
        assertEquals(-350.0, after.get("Diya"));
        assertEquals(List.of("Diya pays Chirag Rs 200.00", "Diya pays Bhavya Rs 150.00"),
                balanceService.getSettlements(g.getId()).stream()
                        .map(SettlementResponse::getMessage).collect(Collectors.toList()));

        // Paying someone more than they are owed is rejected
        assertThrows(InvalidSplitException.class,
                () -> paymentService.recordPayment(g.getId(), payment(d, c, 500)));
    }

    private PaymentRequest payment(User from, User to, double amount) {
        PaymentRequest p = new PaymentRequest();
        p.setFromUserId(from.getId());
        p.setToUserId(to.getId());
        p.setAmount(amount);
        return p;
    }

    private ExpenseRequest request(String desc, double amount, User paidBy, String type, SplitRequest... splits) {
        ExpenseRequest r = new ExpenseRequest();
        r.setDescription(desc);
        r.setAmount(amount);
        r.setPaidBy(paidBy.getId());
        r.setSplitType(type);
        r.setSplits(List.of(splits));
        return r;
    }

    private SplitRequest split(User u, Double value) {
        return new SplitRequest(u.getId(), value);
    }
}
