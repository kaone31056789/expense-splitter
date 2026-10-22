package com.expensesplitter.observer;

import com.expensesplitter.entity.Expense;

/**
 * OBSERVER PATTERN: the "observer" interface.
 * Any class that wants to react to expense events implements this.
 */
public interface ExpenseListener {

    void onExpenseAdded(Expense expense);

    // Java 8 default method: listeners may ignore deletes if they want
    default void onExpenseDeleted(Expense expense) {
    }
}
