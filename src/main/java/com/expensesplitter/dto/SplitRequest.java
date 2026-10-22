package com.expensesplitter.dto;

/**
 * One participant of an expense.
 * value meaning depends on split type:
 *   EQUAL      -> ignored (can be left out)
 *   PERCENTAGE -> percent, e.g. 25
 *   EXACT      -> amount,  e.g. 250
 */
public class SplitRequest {

    private Long userId;
    private Double value;

    public SplitRequest() {
    }

    public SplitRequest(Long userId, Double value) {
        this.userId = userId;
        this.value = value;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
