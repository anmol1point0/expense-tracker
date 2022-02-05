package com.example.expenseTracker.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.example.expenseTracker.DAO.TransactionsRepository;
import com.example.expenseTracker.DAO.UserRepository;
import com.example.expenseTracker.models.Transaction;
import com.example.expenseTracker.models.TransactionRequest;
import com.example.expenseTracker.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ExpenseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    /*
            Purpose: Get the user in DB by their email Address
            input: Email address
            output: User
    */
    private User getUser(String userEmailAddress){
        return userRepository.findFirstByEmailAddress(userEmailAddress);
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

    private void setup(TransactionRequest transactionRequest){
        transactionRequest.setPaymentTimestamp(LocalDateTime.now());
    }

    public void recordTransaction(TransactionRequest transactionRequest){
        setup(transactionRequest);
        transactionsRepository.save(transactionRequest);
    }

    public List<Transaction> getUserTransactions(String uid){
        System.out.println("transactionRepositopry is :" + transactionsRepository);
        List<Transaction> usertransactions = transactionsRepository.findFirstByUid(uid);
        return usertransactions;
    }
    
    public static void main(String[] args){
        ExpenseService userService = new ExpenseService();
        User user = userService.getUser("random");
        Assert.isTrue(user==null, "test passed");
    }
}
