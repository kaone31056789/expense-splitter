package com.expensesplitter.service;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.dto.ExpenseRequest;
import com.expensesplitter.dto.SplitRequest;
import com.expensesplitter.entity.Expense;
import com.expensesplitter.entity.ExpenseShare;
import com.expensesplitter.entity.Group;
import com.expensesplitter.entity.User;
import com.expensesplitter.exception.ExpenseNotFoundException;
import com.expensesplitter.exception.InvalidSplitException;
import com.expensesplitter.observer.ExpenseEventPublisher;
import com.expensesplitter.repository.ExpenseRepository;
import com.expensesplitter.strategy.SplitStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;
    private final UserService userService;
    private final ExpenseEventPublisher eventPublisher;

    // STRATEGY PATTERN: Spring injects every SplitStrategy bean into this map,
    // keyed by bean name -> {"EQUAL": EqualSplitStrategy, "PERCENTAGE": ..., "EXACT": ...}
    private final Map<String, SplitStrategy> strategies;

    private final ConfigService config = ConfigService.getInstance();

    // CONSTRUCTOR INJECTION: dependencies are final and passed in by Spring.
    // With a single constructor, @Autowired is optional (Spring uses it automatically).
    public ExpenseService(ExpenseRepository expenseRepository,
                          GroupService groupService,
                          UserService userService,
                          ExpenseEventPublisher eventPublisher,
                          Map<String, SplitStrategy> strategies) {
        this.expenseRepository = expenseRepository;
        this.groupService = groupService;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.strategies = strategies;
    }

    /**
     * Saves expense + shares + activity log as ONE unit of work.
     * If any step throws a RuntimeException (e.g. InvalidSplitException),
     * Spring rolls back everything, so no half-saved expense is left in the DB.
     */
    @Transactional
    public Expense addExpense(Long groupId, ExpenseRequest request) {
        Group group = groupService.getGroup(groupId);

        // ---- 1. Basic validation ----
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new InvalidSplitException("Amount must be greater than 0");
        }
        if (request.getPaidBy() == null) {
            throw new IllegalArgumentException("paidBy (user id) is required");
        }
        User paidBy = userService.getUser(request.getPaidBy());
        if (!group.hasMember(paidBy.getId())) {
            throw new InvalidSplitException(paidBy.getName() + " is not a member of " + group.getName());
        }

        // ---- 2. Pick the strategy at runtime from the request body ----
        String splitType = request.getSplitType() == null ? "" : request.getSplitType().trim().toUpperCase();
        SplitStrategy strategy = strategies.get(splitType);
        if (strategy == null) {
            throw new InvalidSplitException("Unknown split type '" + request.getSplitType()
                    + "'. Use one of " + strategies.keySet());
        }

        List<SplitRequest> splits = request.getSplits();
        validateParticipants(group, splits);

        double amount = config.round(request.getAmount());

        // ---- 3. Save the expense first ----
        Expense expense = new Expense(request.getDescription().trim(), amount, splitType, paidBy, group);
        expense = expenseRepository.save(expense);

        // ---- 4. Calculate shares. If the split is invalid this throws and
        //         the expense saved in step 3 is ROLLED BACK. ----
        Map<Long, Double> shares = strategy.calculateShares(amount, splits);

        // ---- 5. Save shares (cascade = ALL saves them with the expense) ----
        for (Map.Entry<Long, Double> entry : shares.entrySet()) {
            User user = userService.getUser(entry.getKey());
            expense.addShare(new ExpenseShare(user, entry.getValue()));
        }
        expense = expenseRepository.save(expense);

        // ---- 6. OBSERVER PATTERN: tell all listeners (AuditLogger, NotificationService) ----
        eventPublisher.notifyExpenseAdded(expense);

        return expense;
    }

    // Every participant must exist, be in the group, and appear only once
    private void validateParticipants(Group group, List<SplitRequest> splits) {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("At least one participant is required in 'splits'");
        }
        Set<Long> seen = new HashSet<>();
        for (SplitRequest split : splits) {
            if (split.getUserId() == null) {
                throw new InvalidSplitException("Every split needs a userId");
            }
            User user = userService.getUser(split.getUserId());
            if (!group.hasMember(user.getId())) {
                throw new InvalidSplitException(user.getName() + " is not a member of " + group.getName());
            }
            if (!seen.add(user.getId())) {
                throw new InvalidSplitException(user.getName() + " appears more than once in the split");
            }
        }
    }

    /**
     * All expenses of a group. If userId is given, keep only expenses that the user
     * paid for OR has a share in (filtered with Streams + lambdas).
     */
    @Transactional(readOnly = true)
    public List<Expense> getExpenses(Long groupId, Long userId) {
        groupService.getGroup(groupId);
        List<Expense> expenses = expenseRepository.findByGroup_IdOrderByCreatedAtAsc(groupId);

        if (userId == null) {
            return expenses;
        }
        userService.getUser(userId); // 404 if the user does not exist

        return expenses.stream()
                .filter(e -> e.getPaidBy().getId().equals(userId)
                        || e.getShares().stream().anyMatch(s -> s.getUser().getId().equals(userId)))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteExpense(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        expenseRepository.delete(expense); // shares are deleted too (cascade)
        eventPublisher.notifyExpenseDeleted(expense);
    }
}
