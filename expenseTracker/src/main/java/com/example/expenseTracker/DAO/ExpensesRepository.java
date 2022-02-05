package com.example.expenseTracker.DAO;

import java.util.List;

import com.example.expenseTracker.models.Expense;
import com.example.expenseTracker.models.ExpenseRequest;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

@Component
public interface ExpensesRepository extends MongoRepository<ExpenseRequest, String> {

    @Query("{uid : ?0, isExpensePayment:?1}")
    List<Expense> findByUidAndExpense(String uid, Boolean expensePayment);

    @Query("{uid : ?0, isSettled: ?1}")
    List<Expense> findByUidAndDues(String uid, Boolean isSettled);
}