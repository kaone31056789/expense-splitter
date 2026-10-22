package com.expensesplitter.dto;

/**
 * Net balance of one member in a group.
 * balance > 0 -> gets money back, balance < 0 -> owes money, 0 -> settled
 */
public class BalanceResponse {

    private Long userId;
    private String name;
    private double balance;
    private String status;

    public BalanceResponse() {
    }

    public BalanceResponse(Long userId, String name, double balance, String status) {
        this.userId = userId;
        this.name = name;
        this.balance = balance;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
