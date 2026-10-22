package com.expensesplitter.dto;

/**
 * JSON body for POST /api/groups/{groupId}/payments
 * { "fromUserId": 4, "toUserId": 1, "amount": 500 }
 */
public class PaymentRequest {

    private Long fromUserId;
    private Long toUserId;
    private Double amount;

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
