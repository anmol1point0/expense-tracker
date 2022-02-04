package com.example.expenseTracker.models;

import java.util.List;

import com.example.expenseTracker.DAO.TrasactionsRepository;
import com.example.expenseTracker.DAO.UserRepository;

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
public class User {

    @Id
    private String uid;
    private String emailAddress;
    private String userName;

    @Autowired
    private TrasactionsRepository trasactionsRepository;

    public List<Transaction> getUserTransactions(){
        System.out.println("transactionRepositopry is :" + trasactionsRepository);
        List<Transaction> usertransactions = trasactionsRepository.findFirstByUid(this.uid);
        return usertransactions;
    }
}
