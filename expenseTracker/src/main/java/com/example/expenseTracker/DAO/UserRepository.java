package com.example.expenseTracker.DAO;

import com.example.expenseTracker.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {

    User findFirstByEmailAddress(String emailAddress);
}