package com.expensesplitter.strategy;

import com.expensesplitter.dto.SplitRequest;

import java.util.List;
import java.util.Map;

/**
 * STRATEGY PATTERN: one interface, many interchangeable algorithms.
 * Each implementation is a Spring bean whose name is the split type
 * ("EQUAL", "PERCENTAGE", "EXACT"). ExpenseService receives all of them
 * as a Map<String, SplitStrategy> and picks one at runtime.
 */
public interface SplitStrategy {

    /**
     * @param amount total expense amount
     * @param splits participants (and their value, if the type needs one)
     * @return map of userId -> amount that user owes (rounded to 2 decimals)
     * @throws com.expensesplitter.exception.InvalidSplitException if the split is not valid
     */
    Map<Long, Double> calculateShares(double amount, List<SplitRequest> splits);
}
