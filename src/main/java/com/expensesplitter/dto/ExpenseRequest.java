package com.expensesplitter.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON body for POST /api/groups/{groupId}/expenses
 * {
 *   "description": "Dinner",
 *   "amount": 1200,
 *   "paidBy": 1,
 *   "splitType": "EQUAL",
 *   "splits": [ {"userId": 1}, {"userId": 2} ]
 * }
 */
public class ExpenseRequest {

    private String description;
    private Double amount;
    private Long paidBy;
    private String splitType;
    private List<SplitRequest> splits = new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(Long paidBy) {
        this.paidBy = paidBy;
    }

    public String getSplitType() {
        return splitType;
    }

    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }

    public List<SplitRequest> getSplits() {
        return splits;
    }

    public void setSplits(List<SplitRequest> splits) {
        this.splits = splits;
    }
}
