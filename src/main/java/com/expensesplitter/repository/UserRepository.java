package com.expensesplitter.repository;

import com.expensesplitter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JpaRepository gives save(), findById(), findAll(), deleteById() ... for free.
 * Spring Data generates the SQL for existsByEmail from the method name.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
