package com.expensesplitter.service;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.dto.BalanceResponse;
import com.expensesplitter.dto.PaymentRequest;
import com.expensesplitter.entity.ActivityLog;
import com.expensesplitter.entity.Group;
import com.expensesplitter.entity.Payment;
import com.expensesplitter.entity.User;
import com.expensesplitter.exception.InvalidSplitException;
import com.expensesplitter.exception.PaymentNotFoundException;
import com.expensesplitter.repository.ActivityLogRepository;
import com.expensesplitter.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Records settle-up payments ("Diya paid Aman Rs 500") and lets them be undone.
 */
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private final ConfigService config = ConfigService.getInstance();

    /**
     * A payment can be partial, but it cannot be more than the payer owes
     * or more than the receiver is owed (otherwise balances would flip sign).
     */
    @Transactional
    public Payment recordPayment(Long groupId, PaymentRequest request) {
        Group group = groupService.getGroup(groupId);

        if (request.getFromUserId() == null || request.getToUserId() == null) {
            throw new IllegalArgumentException("fromUserId and toUserId are required");
        }
        if (request.getFromUserId().equals(request.getToUserId())) {
            throw new IllegalArgumentException("A member cannot pay themselves");
        }
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        User from = userService.getUser(request.getFromUserId());
        User to = userService.getUser(request.getToUserId());
        if (!group.hasMember(from.getId()) || !group.hasMember(to.getId())) {
            throw new IllegalArgumentException("Both people must be members of " + group.getName());
        }

        double amount = config.round(request.getAmount());
        double fromBalance = balanceOf(groupId, from.getId());
        double toBalance = balanceOf(groupId, to.getId());

        if (amount > -fromBalance + config.getTolerance()) {
            throw new InvalidSplitException(from.getName() + " only owes " + config.format(Math.max(0, -fromBalance)));
        }
        if (amount > toBalance + config.getTolerance()) {
            throw new InvalidSplitException(to.getName() + " is only owed " + config.format(Math.max(0, toBalance)));
        }

        Payment payment = paymentRepository.save(new Payment(group, from, to, amount));
        activityLogRepository.save(new ActivityLog(groupId,
                from.getName() + " paid " + to.getName() + " " + config.format(amount)));
        return payment;
    }

    public List<Payment> getPayments(Long groupId) {
        groupService.getGroup(groupId); // 404 if the group does not exist
        return paymentRepository.findByGroup_IdOrderByCreatedAtDesc(groupId);
    }

    // Undo a payment that was recorded by mistake
    @Transactional
    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        paymentRepository.delete(payment);
        activityLogRepository.save(new ActivityLog(payment.getGroupId(), "Payment of "
                + config.format(payment.getAmount()) + " from " + payment.getFromUser().getName()
                + " to " + payment.getToUser().getName() + " was undone"));
    }

    // Current net balance of one member (lambda + stream over the balance list)
    private double balanceOf(Long groupId, Long userId) {
        return balanceService.getBalances(groupId).stream()
                .filter(b -> b.getUserId().equals(userId))
                .mapToDouble(BalanceResponse::getBalance)
                .findFirst()
                .orElse(0);
    }
}
