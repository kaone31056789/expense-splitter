package com.expensesplitter.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * A settle-up payment: "fromUser paid toUser Rs amount" outside the app (cash/UPI).
 * Recording it moves both members' balances towards zero.
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonIgnore
    private Group group;

    // The person who paid (was a debtor)
    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    // The person who received the money (was a creditor)
    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    @Column(nullable = false)
    private double amount;

    private LocalDateTime createdAt;

    public Payment() {
    }

    public Payment(Group group, User fromUser, User toUser, double amount) {
        this.group = group;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
