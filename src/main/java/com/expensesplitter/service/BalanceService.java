package com.expensesplitter.service;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.dto.BalanceResponse;
import com.expensesplitter.dto.SettlementResponse;
import com.expensesplitter.entity.Expense;
import com.expensesplitter.entity.ExpenseShare;
import com.expensesplitter.entity.Group;
import com.expensesplitter.entity.Payment;
import com.expensesplitter.repository.ExpenseRepository;
import com.expensesplitter.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Service
public class BalanceService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private GroupService groupService;

    private final ConfigService config = ConfigService.getInstance();

    /**
     * Net balance = (expenses PAID - shares OWED) + (settle-up payments SENT - payments RECEIVED).
     * Positive -> others owe them. Negative -> they owe others.
     */
    @Transactional(readOnly = true)
    public List<BalanceResponse> getBalances(Long groupId) {
        Group group = groupService.getGroup(groupId);
        List<Expense> expenses = expenseRepository.findByGroup_IdOrderByCreatedAtAsc(groupId);
        List<Payment> payments = paymentRepository.findByGroup_IdOrderByCreatedAtDesc(groupId);

        // userId -> total paid   (Streams: groupingBy into a HashMap + summingDouble)
        Map<Long, Double> paid = expenses.stream()
                .collect(Collectors.groupingBy(e -> e.getPaidBy().getId(), HashMap::new,
                        Collectors.summingDouble(Expense::getAmount)));

        // userId -> total owed   (flatMap turns each expense into its list of shares)
        Map<Long, Double> owed = expenses.stream()
                .flatMap(e -> e.getShares().stream())
                .collect(Collectors.groupingBy(s -> s.getUser().getId(), HashMap::new,
                        Collectors.summingDouble(ExpenseShare::getShareAmount)));

        // Settle-up payments: paying someone reduces your debt, receiving reduces your credit
        Map<Long, Double> sent = payments.stream()
                .collect(Collectors.groupingBy(p -> p.getFromUser().getId(), HashMap::new,
                        Collectors.summingDouble(Payment::getAmount)));
        Map<Long, Double> received = payments.stream()
                .collect(Collectors.groupingBy(p -> p.getToUser().getId(), HashMap::new,
                        Collectors.summingDouble(Payment::getAmount)));

        // Build one row per member, sorted from highest balance to lowest
        return group.getMembers().stream()
                .map(member -> {
                    Long id = member.getId();
                    double net = config.round(paid.getOrDefault(id, 0.0) - owed.getOrDefault(id, 0.0)
                            + sent.getOrDefault(id, 0.0) - received.getOrDefault(id, 0.0));
                    return new BalanceResponse(member.getId(), member.getName(), net, statusOf(net));
                })
                .sorted(Comparator.comparingDouble(BalanceResponse::getBalance).reversed()
                        .thenComparing(BalanceResponse::getName))
                .collect(Collectors.toList());
    }

    private String statusOf(double net) {
        if (net > config.getTolerance()) {
            return "gets back " + config.format(net);
        } else if (net < -config.getTolerance()) {
            return "owes " + config.format(-net);
        }
        return "settled up";
    }

    /**
     * Cash-flow settlement (greedy). Uses at most (n - 1) payments; usually, but not always,
     * the true minimum (finding the exact minimum is an NP-hard problem).
     * 1. Put creditors (balance > 0) in a max-heap and debtors (balance < 0) in a max-heap
     *    of how much they owe.
     * 2. Repeatedly match the BIGGEST creditor with the BIGGEST debtor.
     *    The debtor pays min(credit, debt). Whoever still has money left goes back in the heap.
     * 3. Every step settles at least one person, so we need at most (n - 1) payments.
     */
    @Transactional(readOnly = true)
    public List<SettlementResponse> getSettlements(Long groupId) {
        List<BalanceResponse> balances = getBalances(groupId);

        // Max-heaps: the lambda comparator puts the LARGEST amount at the top
        PriorityQueue<Party> creditors = new PriorityQueue<>((a, b) -> Double.compare(b.amount, a.amount));
        PriorityQueue<Party> debtors = new PriorityQueue<>((a, b) -> Double.compare(b.amount, a.amount));

        for (BalanceResponse b : balances) {
            if (b.getBalance() > config.getTolerance()) {
                creditors.add(new Party(b.getUserId(), b.getName(), b.getBalance()));
            } else if (b.getBalance() < -config.getTolerance()) {
                debtors.add(new Party(b.getUserId(), b.getName(), -b.getBalance()));
            }
        }

        List<SettlementResponse> result = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Party creditor = creditors.poll();
            Party debtor = debtors.poll();

            double pay = config.round(Math.min(creditor.amount, debtor.amount));
            String message = debtor.name + " pays " + creditor.name + " " + config.format(pay);
            result.add(new SettlementResponse(debtor.userId, debtor.name,
                    creditor.userId, creditor.name, pay, message));

            // Whoever still has money left goes back into their heap
            creditor.amount = config.round(creditor.amount - pay);
            debtor.amount = config.round(debtor.amount - pay);
            if (creditor.amount > config.getTolerance()) {
                creditors.add(creditor);
            }
            if (debtor.amount > config.getTolerance()) {
                debtors.add(debtor);
            }
        }
        return result;
    }

    // Small helper used only inside the settlement algorithm
    private static class Party {
        Long userId;
        String name;
        double amount; // how much this person should receive (creditor) or pay (debtor)

        Party(Long userId, String name, double amount) {
            this.userId = userId;
            this.name = name;
            this.amount = amount;
        }
    }
}
