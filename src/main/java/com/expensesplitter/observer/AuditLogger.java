package com.expensesplitter.observer;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.entity.ActivityLog;
import com.expensesplitter.entity.Expense;
import com.expensesplitter.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Observer #1: stores an activity log row in the database.
 * It runs inside the same @Transactional addExpense call, so if anything fails
 * the log row is rolled back together with the expense.
 */
@Component
public class AuditLogger implements ExpenseListener {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private final ConfigService config = ConfigService.getInstance();

    @Override
    public void onExpenseAdded(Expense expense) {
        String message = expense.getPaidBy().getName() + " added expense '" + expense.getDescription()
                + "' of " + config.format(expense.getAmount()) + " (" + expense.getSplitType() + " split)";
        activityLogRepository.save(new ActivityLog(expense.getGroupId(), message));
    }

    @Override
    public void onExpenseDeleted(Expense expense) {
        String message = "Expense '" + expense.getDescription() + "' of "
                + config.format(expense.getAmount()) + " was deleted";
        activityLogRepository.save(new ActivityLog(expense.getGroupId(), message));
    }
}
