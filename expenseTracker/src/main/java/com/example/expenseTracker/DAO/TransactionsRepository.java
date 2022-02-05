package com.example.expenseTracker.DAO;

import java.util.List;

import com.example.expenseTracker.models.Transaction;
import com.example.expenseTracker.models.TransactionRequest;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface TransactionsRepository extends MongoRepository<TransactionRequest, String> {
    List<Transaction> findFirstByUid(String uid);

    @Query("{uid : ?0, dueSettled : false}")
    List<Transaction> findFirstByUidAndDueSettled(String uid);
}