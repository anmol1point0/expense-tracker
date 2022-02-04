package com.example.expenseTracker.DAO;

import java.util.List;

import com.example.expenseTracker.models.Transaction;
import com.example.expenseTracker.models.TransactionRequest;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrasactionsRepository extends MongoRepository<TransactionRequest, String> {
    List<Transaction> findFirstByUid(String uid);
}