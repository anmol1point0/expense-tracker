package com.example.expenseTracker.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonIgnoreProperties
public class Due {

    private String uid;
    private String name;
    private BigDecimal duePayment;
    private LocalDate dueDate;

    public Due(String uid, String name, BigDecimal duePayment, LocalDate dueDate){
        this.uid = uid;
        this.name = name;
        this.duePayment = duePayment;
        this.dueDate = dueDate;
    }
}
