package com.expensesplitter.service;

import com.expensesplitter.entity.Group;
import com.expensesplitter.entity.User;
import com.expensesplitter.exception.UserInUseException;
import com.expensesplitter.exception.UserNotFoundException;
import com.expensesplitter.repository.ExpenseRepository;
import com.expensesplitter.repository.ExpenseShareRepository;
import com.expensesplitter.repository.PaymentRepository;
import com.expensesplitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    // Field injection (simple style). ExpenseService shows constructor injection.
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("A valid email is required");
        }
        String email = user.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        return userRepository.save(new User(user.getName().trim(), email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        // orElseThrow with a lambda: throw our custom exception if not found
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Deletes a user and removes them from all their groups.
     * Refused (409) if the user paid for or shares in any expense, or is part of a payment, because deleting
     * those expenses would silently change everyone else's balances.
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getUser(id);

        long involved = expenseRepository.countByPaidBy_Id(id) + expenseShareRepository.countByUser_Id(id)
                + paymentRepository.countByFromUser_IdOrToUser_Id(id, id);
        if (involved > 0) {
            throw new UserInUseException(user.getName() + " is still part of expenses or payments. "
                    + "Delete those (or the whole group) first.");
        }

        // Remove the user from the owning side (Group.members) so join-table rows are deleted.
        // Copy into a new list first because we change the set while looping.
        for (Group group : new ArrayList<>(user.getGroups())) {
            group.getMembers().remove(user);
        }
        user.getGroups().clear();
        userRepository.delete(user);
    }
}
