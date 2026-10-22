package com.expensesplitter.observer;

import com.expensesplitter.entity.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER PATTERN: the "subject".
 * Spring finds every bean that implements ExpenseListener and injects them as a list.
 * Adding a new listener later = just create a new @Component class, no change here.
 */
@Component
public class ExpenseEventPublisher {

    private final List<ExpenseListener> listeners = new ArrayList<>();

    @Autowired
    public void setListeners(List<ExpenseListener> listeners) {
        this.listeners.addAll(listeners);
    }

    public void notifyExpenseAdded(Expense expense) {
        listeners.forEach(listener -> listener.onExpenseAdded(expense));
    }

    public void notifyExpenseDeleted(Expense expense) {
        listeners.forEach(listener -> listener.onExpenseDeleted(expense));
    }
}
