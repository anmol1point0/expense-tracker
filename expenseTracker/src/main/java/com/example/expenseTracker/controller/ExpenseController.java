package com.example.expenseTracker.controller;

import java.util.Objects;

import com.example.expenseTracker.models.User;
import com.example.expenseTracker.services.UserService;

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
    private UserService userService;

    private User user;
    
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user){
        Boolean userRegistered = userService.registerUser(user);
        if(userRegistered){
            return new ResponseEntity<>("User: " + user.getUserName() + " successfully registered" ,HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("User: " + user.getUserName() + " Already Present, Please login" ,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody String emailAddress){
        user = userService.login(emailAddress);
        if(Objects.isNull(user)){
            return new ResponseEntity<>("Email: " + emailAddress + " not Present in DB, Please register First" ,HttpStatus.BAD_REQUEST);
        }
        else{
            return new ResponseEntity<>("User: " + user.getUserName() + " logged in sucessfully" ,HttpStatus.OK);
        }
    }
}
