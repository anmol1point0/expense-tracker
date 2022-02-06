package com.example.expenseTracker.controller;

import java.util.List;
import java.util.Objects;

import com.example.expenseTracker.models.Due;
import com.example.expenseTracker.models.DueRequest;
import com.example.expenseTracker.models.Expense;
import com.example.expenseTracker.models.ExpenseRequest;
import com.example.expenseTracker.models.ExpenseType;
import com.example.expenseTracker.models.User;
import com.example.expenseTracker.services.ExpenseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    private User user;

    @PostMapping("/registerUser")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        Boolean userRegistered = expenseService.registerUser(user);
        if (userRegistered) {
            return new ResponseEntity<>("User: " + user.getUserName() + " successfully registered", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User: " + user.getUserName() + " Already Present, Please login",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String emailAddress) {
        user = expenseService.login(emailAddress);
        if (Objects.isNull(user)) {
            return new ResponseEntity<>("Email: " + emailAddress + " not Present in DB, Please register First",
                    HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("User: " + user.getUserName() + " logged in sucessfully", HttpStatus.OK);
        }
    }

    @PostMapping("/expense")
    public ResponseEntity<String> recordExpense(@RequestBody ExpenseRequest transactionRequest) {
        if (Objects.isNull(user)) {
            return new ResponseEntity<>("Please login to record a transaction ", HttpStatus.BAD_REQUEST);
        }
        transactionRequest.setExpenseType(ExpenseType.EXPENSE);
        transactionRequest.setUid(user.getUid());
        expenseService.recordTransaction(transactionRequest);
        return new ResponseEntity<>("Transaction is recorded succesfully for user: " + user.getUserName(),
                HttpStatus.OK);

    }

    @GetMapping("/expense")
    public ResponseEntity<List<Expense>> getUserExpenseHistory() {
        List<Expense> userTransactions;
        if (Objects.isNull(user)) {
            userTransactions = null;
            return new ResponseEntity<>(userTransactions, HttpStatus.BAD_REQUEST);
        } else {
            userTransactions = user.getUserExpenses(expenseService);
            return new ResponseEntity<>(userTransactions, HttpStatus.OK);
        }
    }

    @GetMapping("/dues")
    public ResponseEntity<List<Due>> getUserUnsettledTransactions() {
        List<Due> userUnsettledDues;
        if (Objects.isNull(user)) {
            userUnsettledDues = null;
            return new ResponseEntity<>(userUnsettledDues, HttpStatus.BAD_REQUEST);
        } else {
            userUnsettledDues = user.getUserDues(expenseService);
            return new ResponseEntity<>(userUnsettledDues, HttpStatus.OK);
        }
    }

    @PostMapping("/dues")
    public ResponseEntity<List<Due>> duesSettle(@RequestBody DueRequest dueRequest) {
        List<Due> userUnsettledDues;
        if (Objects.isNull(user)) {
            userUnsettledDues = null;
            return new ResponseEntity<>(userUnsettledDues, HttpStatus.BAD_REQUEST);
        } else {
            userUnsettledDues = expenseService.settleDues(user.getUid(), dueRequest.getAmount(),
                    dueRequest.getDuePaymentStrategy(), dueRequest.getPaymentGateway());
        }
        if (Objects.isNull(userUnsettledDues)) {
            return new ResponseEntity<>(userUnsettledDues, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(userUnsettledDues, HttpStatus.OK);
        }
    }

    @GetMapping("/duesHistory")
    public ResponseEntity<List<Expense>> getSettlesDuesList() {
        List<Expense> settledDues;
        if (Objects.isNull(user)) {
            settledDues = null;
            return new ResponseEntity<>(settledDues, HttpStatus.BAD_REQUEST);
        } else {
            settledDues = user.getUserSettledDues(expenseService);
            return new ResponseEntity<>(settledDues, HttpStatus.OK);
        }
    }
}
