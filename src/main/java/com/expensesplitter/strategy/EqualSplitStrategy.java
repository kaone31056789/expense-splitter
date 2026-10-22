package com.expensesplitter.strategy;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.dto.SplitRequest;
import com.expensesplitter.exception.InvalidSplitException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * EQUAL: amount / number of participants.
 * Rounding leftover (e.g. 1000 / 3 = 333.33 x 3 = 999.99) is added to the first person
 * so that the shares always add up to the exact amount.
 */
@Component("EQUAL")
public class EqualSplitStrategy implements SplitStrategy {

    private final ConfigService config = ConfigService.getInstance();

    @Override
    public Map<Long, Double> calculateShares(double amount, List<SplitRequest> splits) {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("EQUAL split needs at least one participant");
        }

        double each = config.round(amount / splits.size());
        double leftover = config.round(amount - each * splits.size());

        // LinkedHashMap keeps the participants in the order they were sent
        Map<Long, Double> shares = new LinkedHashMap<>();
        for (SplitRequest split : splits) {
            shares.put(split.getUserId(), each);
        }

        Long firstUser = splits.get(0).getUserId();
        shares.put(firstUser, config.round(shares.get(firstUser) + leftover));
        return shares;
    }
}
