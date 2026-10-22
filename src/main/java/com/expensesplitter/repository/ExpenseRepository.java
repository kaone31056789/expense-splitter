package com.expensesplitter.repository;

import com.expensesplitter.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Derived query: SELECT * FROM expenses WHERE group_id = ? ORDER BY created_at
    // "Group_Id" means: follow the 'group' relation, then use its 'id'
    List<Expense> findByGroup_IdOrderByCreatedAtAsc(Long groupId);

    // How many expenses this user paid for (used before deleting a user)
    long countByPaidBy_Id(Long userId);
}
