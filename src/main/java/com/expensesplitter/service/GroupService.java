package com.expensesplitter.service;

import com.expensesplitter.entity.ActivityLog;
import com.expensesplitter.entity.Group;
import com.expensesplitter.entity.User;
import com.expensesplitter.exception.GroupNotFoundException;
import com.expensesplitter.repository.ActivityLogRepository;
import com.expensesplitter.repository.ExpenseRepository;
import com.expensesplitter.repository.PaymentRepository;
import com.expensesplitter.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Group createGroup(Group group) {
        if (group.getName() == null || group.getName().isBlank()) {
            throw new IllegalArgumentException("Group name is required");
        }
        Group saved = groupRepository.save(new Group(group.getName().trim()));
        activityLogRepository.save(new ActivityLog(saved.getId(), "Group '" + saved.getName() + "' created"));
        return saved;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroup(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new GroupNotFoundException(id));
    }

    @Transactional
    public Group addMember(Long groupId, Long userId) {
        Group group = getGroup(groupId);
        User user = userService.getUser(userId);

        if (group.hasMember(userId)) {
            throw new IllegalArgumentException(user.getName() + " is already a member of " + group.getName());
        }
        group.addMember(user);
        activityLogRepository.save(new ActivityLog(groupId, user.getName() + " joined the group"));
        return groupRepository.save(group);
    }

    /**
     * Deletes a group with everything inside it: expenses (and their shares, by cascade),
     * settle-up payments, member links and the activity log. Users themselves are kept.
     * @Transactional: if any step fails, nothing is deleted.
     */
    @Transactional
    public void deleteGroup(Long groupId) {
        Group group = getGroup(groupId);
        expenseRepository.deleteAll(expenseRepository.findByGroup_IdOrderByCreatedAtAsc(groupId));
        paymentRepository.deleteAll(paymentRepository.findByGroup_IdOrderByCreatedAtDesc(groupId));
        for (User member : group.getMembers()) {
            member.getGroups().remove(group);
        }
        group.getMembers().clear();
        activityLogRepository.deleteByGroupId(groupId);
        groupRepository.delete(group);
    }

    public List<ActivityLog> getActivity(Long groupId) {
        getGroup(groupId); // 404 if the group does not exist
        return activityLogRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
    }
}
