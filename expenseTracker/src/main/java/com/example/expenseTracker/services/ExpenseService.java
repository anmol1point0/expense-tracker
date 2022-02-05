package com.example.expenseTracker.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.expenseTracker.DAO.ExpensesRepository;
import com.example.expenseTracker.DAO.UserRepository;
import com.example.expenseTracker.models.Due;
import com.example.expenseTracker.models.Expense;
import com.example.expenseTracker.models.ExpenseRequest;
import com.example.expenseTracker.models.User;

import org.apache.tomcat.jni.Local;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ExpenseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpensesRepository expensesRepository;

    // <ExpenseRequest, Due> List<Due> mapList(List<ExpenseRequest> source,
    // Class<Due> targetClass) {
    // return source.stream().map(element -> modelMapper.map(element,
    // targetClass)).collect(Collectors.toList());
    // }

    /*
     * Purpose: Get the user in DB by their email Address
     * input: Email address
     * output: User
     */
    private User getUser(String userEmailAddress) {
        return userRepository.findByEmailAddress(userEmailAddress);
    }

    /*
     * Purpose: Register the user if not already present in the DB
     * input: User
     * output:
     * true : if User is successfully registered
     * false: if user is already present.
     */
    public Boolean registerUser(User user) {
        if (!Objects.nonNull(getUser(user.getEmailAddress()))) {
            userRepository.save(user);
            return true;
        }

        else {
            return false;
        }
    }

    /*
     * Purpose: Login the incoming user if present in DB
     * input: email Address
     * output: User
     */
    public User login(String emailAddress) {

        User user = getUser(emailAddress);
        if (Objects.nonNull(user)) {
            return user;
        } else {
            return null;
        }
    }

    private void setup(ExpenseRequest transactionRequest) {
        transactionRequest.setPaymentTimestamp(LocalDateTime.now());
    }

    private void setupForDue(ExpenseRequest expenseRequest, Boolean isSettled) {
        expenseRequest.setIsSettled(isSettled);
    }

    public void recordTransaction(ExpenseRequest expenseRequest) {
        setup(expenseRequest);
        BigDecimal expense = expenseRequest.getExpense();
        BigDecimal paidAmount = expenseRequest.getPaidAmount();
        BigDecimal due = expense.subtract(paidAmount);
        expenseRequest.setDuePayment(due);
        if (due.doubleValue() > 0.0) {
            setupForDue(expenseRequest, false);
        } else {
            setupForDue(expenseRequest, true);
        }

        expensesRepository.save(expenseRequest);
    }

    public List<Expense> getUserExpenses(String uid) {
        System.out.println("transactionRepositopry is :" + expensesRepository);
        System.out.println("uid: " + uid);
        List<Expense> usertransactions = expensesRepository.findByUidAndExpense(uid, true);
        return usertransactions;
    }

    public List<Due> getAllDues(String uid) {
        List<Expense> unsettledTransactions = expensesRepository.findByUidAndDues(uid, false);
        ModelMapper modelMapper = new ModelMapper();
        List<Due> usertransactions = unsettledTransactions.stream()
                .map(unsettledTransaction -> modelMapper.map(unsettledTransaction,
                        Due.class))
                .collect(Collectors.toList());
        return usertransactions;
    }

    private void setupAndInsertDuePaidRequest(ExpenseRequest expenseRequest, BigDecimal duePayment,
            String paymentGateway) {
        // Inserting the payment of this due
        ExpenseRequest duePaidRequest = new ExpenseRequest(expenseRequest);
        System.out.println("due paid request: " + duePaidRequest.getPaidAmount());
        duePaidRequest.setExpense(duePayment);
        duePaidRequest.setPaidAmount(duePayment);
        duePaidRequest.setPaymentTimestamp(LocalDateTime.now());
        duePaidRequest.setIsExpensePayment(false);
        duePaidRequest.setPaymentMethod(paymentGateway);
        duePaidRequest.setIsSettled(true);
        duePaidRequest.setDuePayment(BigDecimal.valueOf(0.0));
        expensesRepository.save(duePaidRequest);
    }

    private boolean duesSettleInorder(List<Due> userDues, BigDecimal amount, String paymentGateway) {
        for (Due userDue : userDues) {
            System.out.println("user is :" + userDue.getName());
            BigDecimal duePayment = userDue.getDuePayment();
            String expenseId = userDue.getExpenseId();
            Optional<ExpenseRequest> expenseRequestOptional = expensesRepository.findById(expenseId);
            if (amount.doubleValue() <= 0.0)
                break;
            else if (expenseRequestOptional.isPresent()) {
                ExpenseRequest expenseRequest = expenseRequestOptional.get();
                BigDecimal paidAmount = expenseRequest.getPaidAmount();
                // If remaining amount is more than due amount, pay in full.
                if (amount.compareTo(duePayment) >= 0) {
                    System.out.println("full present");
                    expenseRequest.setIsSettled(true);
                    expenseRequest.setPaidAmount(paidAmount.add(duePayment));
                    expenseRequest.setDuePayment(BigDecimal.valueOf(0.0));
                    expensesRepository.save(expenseRequest);
                    setupAndInsertDuePaidRequest(expenseRequest, duePayment, paymentGateway);
                    amount = amount.subtract(duePayment);
                }
                // if smaller, pay maximum
                else if (amount.compareTo(duePayment) < 0) {
                    System.out.println("partial present");
                    expenseRequest.setPaidAmount(paidAmount.add(amount));
                    expenseRequest.setDuePayment(duePayment.subtract(amount));
                    System.out.println("paid amount: " + expenseRequest.getPaidAmount());
                    expensesRepository.save(expenseRequest);
                    setupAndInsertDuePaidRequest(expenseRequest, amount, paymentGateway);
                    amount = BigDecimal.valueOf(0.0);
                } else
                    break;
                System.out.println("Value of Amount: " + amount.doubleValue());
            } else {
                return false;
            }
        }
        return true;

    }

    /*
     * method
     * 1: First in first out - the due added first will be paid first
     * 2: Latest repayment date first - the due that is nearest will be paid first
     */
    public List<Due> settleDues(String uid, BigDecimal amount, Integer duePaymentStrategy, String paymentGateway) {
        // Getting all the unsettled transactions for a given user.
        List<Due> userDues = getAllDues(uid);
        Boolean dueSettled = false;
        switch (duePaymentStrategy) {
            case 1:
                System.out.println("Reached inside switch");
                dueSettled = duesSettleInorder(userDues, amount, paymentGateway);
                break;
            case 2:
                Collections.sort(userDues, Due.DueDateComparator);
                dueSettled = duesSettleInorder(userDues, amount, paymentGateway);
                break;
        }
        if (dueSettled) {
            return getAllDues(uid);
        } else
            return null;
    }

    public List<Expense> getAllSettledDues(String uid) {
        return expensesRepository.findByUidAndDues(uid, true);
    }

    public static void main(String[] args) {
        ExpenseService userService = new ExpenseService();
        User user = userService.getUser("random");
        Assert.isTrue(user == null, "test passed");
    }
}
