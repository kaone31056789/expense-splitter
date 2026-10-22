package com.expensesplitter.strategy;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.dto.SplitRequest;
import com.expensesplitter.exception.InvalidSplitException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * EXACT: each participant's amount is given directly. Amounts must total the expense amount.
 */
@Component("EXACT")
public class ExactSplitStrategy implements SplitStrategy {

    private final ConfigService config = ConfigService.getInstance();

    @Override
    public Map<Long, Double> calculateShares(double amount, List<SplitRequest> splits) {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("EXACT split needs at least one participant");
        }

        boolean missingValue = splits.stream().anyMatch(s -> s.getValue() == null || s.getValue() < 0);
        if (missingValue) {
            throw new InvalidSplitException("Every participant needs an exact amount of 0 or more");
        }

        double total = splits.stream().mapToDouble(SplitRequest::getValue).sum();
        if (Math.abs(total - amount) > config.getTolerance()) {
            throw new InvalidSplitException("Exact amounts add up to " + config.format(total)
                    + " but the expense is " + config.format(amount));
        }

        Map<Long, Double> shares = new LinkedHashMap<>();
        splits.forEach(s -> shares.put(s.getUserId(), config.round(s.getValue())));
        return shares;
    }
}
