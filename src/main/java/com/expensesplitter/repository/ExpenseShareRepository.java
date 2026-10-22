package com.expensesplitter.repository;

import com.expensesplitter.entity.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {

    // How many expense shares belong to this user (used before deleting a user)
    long countByUser_Id(Long userId);
}
