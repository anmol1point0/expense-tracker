package com.example.expenseTracker.controller;

import java.util.List;
import java.util.Objects;

import com.example.expenseTracker.models.Transaction;
import com.example.expenseTracker.models.TransactionRequest;
import com.example.expenseTracker.models.User;
import com.example.expenseTracker.services.ExpenseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    private User user;
    
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user){
        Boolean userRegistered = expenseService.registerUser(user);
        if(userRegistered){
            return new ResponseEntity<>("User: " + user.getUserName() + " successfully registered" ,HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("User: " + user.getUserName() + " Already Present, Please login" ,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody String emailAddress){
        user = expenseService.login(emailAddress);
        if(Objects.isNull(user)){
            return new ResponseEntity<>("Email: " + emailAddress + " not Present in DB, Please register First" ,HttpStatus.BAD_REQUEST);
        }
        else{
            return new ResponseEntity<>("User: " + user.getUserName() + " logged in sucessfully" ,HttpStatus.OK);
        }
    }

    @PostMapping("/transaction")
    public ResponseEntity<String> doTransaction(@RequestBody TransactionRequest transactionRequest){
        if(Objects.isNull(user)){
            return new ResponseEntity<>("Please login to record a transaction " ,HttpStatus.BAD_REQUEST);
        }
        System.out.println("transaction request is " + transactionRequest.getDueSettled());
        transactionRequest.setUid(user.getUid());
        expenseService.recordTransaction(transactionRequest);
        return new ResponseEntity<>("Transaction is recorded succesfully for user: " + user.getUserName() ,HttpStatus.OK);
        
    }

   @GetMapping("/transaction")
   public ResponseEntity<List<Transaction>> getUserTransactions(){
       List<Transaction> userTransactions;
       if(Objects.isNull(user)){
            userTransactions = null;
            return new ResponseEntity<>(userTransactions ,HttpStatus.BAD_REQUEST);
       }
       else{
           userTransactions = user.getUserTransactions(expenseService);
           return new ResponseEntity<>(userTransactions ,HttpStatus.OK);
       }
   }
}
