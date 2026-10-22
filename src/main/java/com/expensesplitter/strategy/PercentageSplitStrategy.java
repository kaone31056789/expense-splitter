package com.expensesplitter.strategy;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.dto.SplitRequest;
import com.expensesplitter.exception.InvalidSplitException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PERCENTAGE: each participant pays value% of the amount. Percents must total 100.
 */
@Component("PERCENTAGE")
public class PercentageSplitStrategy implements SplitStrategy {

    private final ConfigService config = ConfigService.getInstance();

    @Override
    public Map<Long, Double> calculateShares(double amount, List<SplitRequest> splits) {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("PERCENTAGE split needs at least one participant");
        }

        // Every participant must send a non-negative percent
        boolean missingValue = splits.stream().anyMatch(s -> s.getValue() == null || s.getValue() < 0);
        if (missingValue) {
            throw new InvalidSplitException("Every participant needs a percentage of 0 or more");
        }

        // Stream + lambda to add up all the percents
        double totalPercent = splits.stream().mapToDouble(SplitRequest::getValue).sum();
        if (Math.abs(totalPercent - 100) > config.getTolerance()) {
            throw new InvalidSplitException("Percentages must add up to 100, but got " + config.round(totalPercent));
        }

        Map<Long, Double> shares = new LinkedHashMap<>();
        double assigned = 0;
        for (SplitRequest split : splits) {
            double share = config.round(amount * split.getValue() / 100);
            shares.put(split.getUserId(), share);
            assigned += share;
        }

        // Put any rounding leftover on the last participant so the total is exact
        Long lastUser = splits.get(splits.size() - 1).getUserId();
        shares.put(lastUser, config.round(shares.get(lastUser) + (amount - assigned)));
        return shares;
    }
}
