package com.expensesplitter.repository;

import com.expensesplitter.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // "Group_Id" follows the 'group' relation, then uses its 'id'
    List<Payment> findByGroup_IdOrderByCreatedAtDesc(Long groupId);

    // Used before deleting a user: are they part of any payment?
    long countByFromUser_IdOrToUser_Id(Long fromUserId, Long toUserId);
}
