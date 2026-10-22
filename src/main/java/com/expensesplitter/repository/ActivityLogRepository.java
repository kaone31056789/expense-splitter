package com.expensesplitter.repository;

import com.expensesplitter.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    // Derived delete query: DELETE FROM activity_logs WHERE group_id = ?
    void deleteByGroupId(Long groupId);
}
