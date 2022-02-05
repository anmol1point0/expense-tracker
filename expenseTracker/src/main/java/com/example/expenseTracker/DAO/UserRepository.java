package com.example.expenseTracker.DAO;

import com.example.expenseTracker.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {
    User findFirstByEmailAddress(String emailAddress);
}