package com.example.expenseTracker.models;

import java.util.List;

import com.example.expenseTracker.DAO.TransactionsRepository;
import com.example.expenseTracker.DAO.UserRepository;
import com.example.expenseTracker.services.ExpenseService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class User {

    @Id
    private String uid;
    private String emailAddress;
    private String userName;

    private ExpenseService expenseService;

    public List<Transaction> getUserTransactions(ExpenseService expenseService){
        this.expenseService = expenseService;
        List<Transaction> usertransactions = expenseService.getUserTransactions(this.getUid());
        return usertransactions;
    }
}
