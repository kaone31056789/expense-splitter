package com.expensesplitter.dto;

/**
 * One payment needed to settle the group, e.g. "Diya pays Aman Rs 400.00"
 */
public class SettlementResponse {

    private Long fromUserId;
    private String fromUser;
    private Long toUserId;
    private String toUser;
    private double amount;
    private String message;

    public SettlementResponse() {
    }

    public SettlementResponse(Long fromUserId, String fromUser, Long toUserId, String toUser,
                              double amount, String message) {
        this.fromUserId = fromUserId;
        this.fromUser = fromUser;
        this.toUserId = toUserId;
        this.toUser = toUser;
        this.amount = amount;
        this.message = message;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
