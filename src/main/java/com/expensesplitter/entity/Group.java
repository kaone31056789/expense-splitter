package com.expensesplitter.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * A group of users who share expenses (e.g. "Goa Trip").
 * Table is named "expense_groups" because "group" is a reserved SQL word.
 */
@Entity
@Table(name = "expense_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Owning side of User <-> Group many-to-many.
    // Creates join table group_members(group_id, user_id).
    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members = new HashSet<>();

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    // Helper that keeps both sides of the relationship in sync
    public void addMember(User user) {
        members.add(user);
        user.getGroups().add(this);
    }

    // Used to check membership by id (works even if objects are different instances)
    public boolean hasMember(Long userId) {
        return members.stream().anyMatch(u -> u.getId().equals(userId));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }
}
