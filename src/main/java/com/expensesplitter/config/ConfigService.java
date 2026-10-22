package com.expensesplitter.config;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * SINGLETON PATTERN (classic GoF version, not a Spring bean).
 * Only one ConfigService object exists in the whole JVM.
 *  - private constructor  -> nobody can call "new ConfigService()"
 *  - private static field -> holds the one instance
 *  - getInstance()        -> global access point (lazy + thread-safe)
 */
public class ConfigService {

    // volatile makes the instance visible correctly across threads
    private static volatile ConfigService instance;

    private final String currencySymbol;
    private final int decimalPlaces;

    // Smallest difference we treat as "zero" when comparing money (half a paisa)
    private final double tolerance;

    private ConfigService() {
        this.currencySymbol = "Rs";
        this.decimalPlaces = 2;
        this.tolerance = 0.01;
    }

    // Double-checked locking: synchronize only the first time
    public static ConfigService getInstance() {
        if (instance == null) {
            synchronized (ConfigService.class) {
                if (instance == null) {
                    instance = new ConfigService();
                }
            }
        }
        return instance;
    }

    /**
     * Rounds money to 2 decimals using BigDecimal (HALF_UP).
     * We use BigDecimal here because plain double math can give 0.1 + 0.2 = 0.30000000000000004.
     */
    public double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // 400 -> "Rs 400.00"
    public String format(double value) {
        return currencySymbol + " " + String.format("%." + decimalPlaces + "f", round(value));
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public double getTolerance() {
        return tolerance;
    }
}
