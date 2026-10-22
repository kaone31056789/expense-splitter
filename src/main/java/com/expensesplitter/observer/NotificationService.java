package com.expensesplitter.observer;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.entity.Expense;
import org.springframework.stereotype.Service;

/**
 * Observer #2: "notifies" the group. In a real app this could send an email or push
 * notification; here we just print to the console.
 */
@Service
public class NotificationService implements ExpenseListener {

    private final ConfigService config = ConfigService.getInstance();

    @Override
    public void onExpenseAdded(Expense expense) {
        System.out.println("[NOTIFICATION] " + expense.getPaidBy().getName()
                + " added expense " + expense.getDescription()
                + " (" + config.format(expense.getAmount()) + ")");
    }
}
