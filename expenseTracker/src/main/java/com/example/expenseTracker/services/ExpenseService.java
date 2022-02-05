package com.example.expenseTracker.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.expenseTracker.DAO.ExpensesRepository;
import com.example.expenseTracker.DAO.UserRepository;
import com.example.expenseTracker.models.Due;
import com.example.expenseTracker.models.Transaction;
import com.example.expenseTracker.models.ExpenseRequest;
import com.example.expenseTracker.models.User;

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

    // <ExpenseRequest, Due> List<Due> mapList(List<ExpenseRequest> source, Class<Due> targetClass) {
    //     return source.stream().map(element -> modelMapper.map(element, targetClass)).collect(Collectors.toList());
    // }
    
    /*
            Purpose: Get the user in DB by their email Address
            input: Email address
            output: User
    */
    private User getUser(String userEmailAddress){
        return userRepository.findByEmailAddress(userEmailAddress);
    }  


    /*
            Purpose: Register the user if not already present in the DB
            input: User 
            output:
                    true : if User is successfully registered
                    false: if user is already present.
    */
    public Boolean registerUser(User user){
        if(!Objects.nonNull(getUser(user.getEmailAddress()))){
            userRepository.save(user);
            return true;
        }

        else{
            return false;
        }
    }

    /*
        Purpose: Login the incoming user if present in DB
            input: email Address
            output: User
    */
    public User login(String emailAddress){
        
        User user = getUser(emailAddress);
        if(Objects.nonNull(user)){
            return user;
        }
        else{
            return null;
        }
    }

    private void setup(ExpenseRequest transactionRequest){
        transactionRequest.setPaymentTimestamp(LocalDateTime.now());
    }

    private void setupForDue(ExpenseRequest expenseRequest, Boolean isSettled){
        expenseRequest.setIsSettled(isSettled);
    }

    public void recordTransaction(ExpenseRequest expenseRequest){
        setup(expenseRequest);
        BigDecimal expense = expenseRequest.getExpense();
        BigDecimal paidAmount = expenseRequest.getPaidAmount();
        BigDecimal due = expense.subtract(paidAmount);
        expenseRequest.setDuePayment(due);
        if(due.doubleValue() > 0.0){
            setupForDue(expenseRequest, false);
        }
        else{
            setupForDue(expenseRequest, true);
        }
        
        expensesRepository.save(expenseRequest);
    }

    public List<Transaction> getUserExpenses(String uid){
        System.out.println("transactionRepositopry is :" + expensesRepository);
        System.out.println("uid: "+uid);
        List<Transaction> usertransactions = expensesRepository.findByUidAndExpense(uid, true);
        return usertransactions;
    }

    public List<Due> getAllDues(String uid){
        List<Transaction> unsettledTransactions = expensesRepository.findByUidAndDueSettled(uid, false);
        ModelMapper modelMapper = new ModelMapper();
        List<Due> usertransactions = unsettledTransactions.stream().map(unsettledTransaction ->modelMapper.map(unsettledTransaction,
        Due.class)).collect(Collectors.toList());
        return usertransactions;
    }

    /*
        method
         1: First in first out - the due added first will be paid first
         2: Latest repayment date first - the due that is nearest will be paid first
    */
    // public void settleDues(String uid, BigDecimal amount, Integer method){
    //     //Getting all the unsettled transactions for a given user.
    //     List<Transaction> userTransactions = getAllUnsettledTransaction(uid);
    //     switch(method){
    //         case 1:
    //             userTransactions = 
    //             break;
            
    //     }
    // }
    
    public static void main(String[] args){
        ExpenseService userService = new ExpenseService();
        User user = userService.getUser("random");
        Assert.isTrue(user==null, "test passed");
    }
}
