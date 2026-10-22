package com.expensesplitter.controller;

import com.expensesplitter.dto.BalanceResponse;
import com.expensesplitter.dto.SettlementResponse;
import com.expensesplitter.entity.ActivityLog;
import com.expensesplitter.entity.Group;
import com.expensesplitter.service.BalanceService;
import com.expensesplitter.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private BalanceService balanceService;

    // POST /api/groups   body: {"name": "Goa Trip"} -> 201 Created
    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        return new ResponseEntity<>(groupService.createGroup(group), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{groupId}")
    public Group getGroup(@PathVariable Long groupId) {
        return groupService.getGroup(groupId);
    }

    // DELETE /api/groups/1 -> 204 No Content (also deletes its expenses and activity log)
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.noContent().build();
    }

    // POST /api/groups/1/members/2 -> 200 OK (returns the updated group)
    @PostMapping("/{groupId}/members/{userId}")
    public Group addMember(@PathVariable Long groupId, @PathVariable Long userId) {
        return groupService.addMember(groupId, userId);
    }

    // GET /api/groups/1/balances -> net balance of every member
    @GetMapping("/{groupId}/balances")
    public List<BalanceResponse> getBalances(@PathVariable Long groupId) {
        return balanceService.getBalances(groupId);
    }

    // GET /api/groups/1/settlements -> minimum list of payments
    @GetMapping("/{groupId}/settlements")
    public List<SettlementResponse> getSettlements(@PathVariable Long groupId) {
        return balanceService.getSettlements(groupId);
    }

    // GET /api/groups/1/activity -> audit log written by the AuditLogger observer
    @GetMapping("/{groupId}/activity")
    public List<ActivityLog> getActivity(@PathVariable Long groupId) {
        return groupService.getActivity(groupId);
    }
}
