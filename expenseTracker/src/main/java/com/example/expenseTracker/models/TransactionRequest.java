package com.example.expenseTracker.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private String uid;
    private BigDecimal expense;
    private BigDecimal paidAmount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private LocalDateTime dueDate;
}
