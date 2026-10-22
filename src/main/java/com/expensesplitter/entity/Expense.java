package com.expensesplitter.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * One bill paid by one user, split among several users.
 */
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double amount;

    // EQUAL, PERCENTAGE or EXACT
    @Column(nullable = false)
    private String splitType;

    private LocalDateTime createdAt;

    // Many expenses can be paid by one user
    @ManyToOne
    @JoinColumn(name = "paid_by_id", nullable = false)
    private User paidBy;

    // Many expenses belong to one group (hidden in JSON, we expose groupId instead)
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonIgnore
    private Group group;

    // One expense has many shares. cascade = ALL: saving/deleting the expense
    // also saves/deletes its shares. orphanRemoval removes shares taken out of the list.
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseShare> shares = new ArrayList<>();

    public Expense() {
    }

    public Expense(String description, double amount, String splitType, User paidBy, Group group) {
        this.description = description;
        this.amount = amount;
        this.splitType = splitType;
        this.paidBy = paidBy;
        this.group = group;
        this.createdAt = LocalDateTime.now();
    }

    // Helper that keeps both sides of the relationship in sync
    public void addShare(ExpenseShare share) {
        shares.add(share);
        share.setExpense(this);
    }

    // Shown in JSON as "groupId"
    public Long getGroupId() {
        return group == null ? null : group.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSplitType() {
        return splitType;
    }

    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(User paidBy) {
        this.paidBy = paidBy;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<ExpenseShare> getShares() {
        return shares;
    }

    public void setShares(List<ExpenseShare> shares) {
        this.shares = shares;
    }
}
