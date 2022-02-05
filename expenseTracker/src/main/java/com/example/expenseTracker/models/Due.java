package com.example.expenseTracker.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties
public class Due implements Comparable<Due> {

    private String uid;
    private String expenseId;
    private String itemName;
    private BigDecimal duePayment;
    private LocalDate dueDate;
    private String recipient;

    public Due(String uid, String itemName, BigDecimal duePayment, LocalDate dueDate) {
        this.uid = uid;
        this.itemName = itemName;
        this.duePayment = duePayment;
        this.dueDate = dueDate;
    }

    @Override
    public int compareTo(Due due) {
        LocalDate compareDueDate = ((Due) due).getDueDate();

        // descending order
        return compareDueDate.compareTo(this.dueDate);
    }

    public static Comparator<Due> DueDateComparator = new Comparator<Due>() {

        public int compare(Due due1, Due due2) {

            LocalDate dueDate1 = due1.getDueDate();
            LocalDate dueDate2 = due2.getDueDate();

            // descending order
            return dueDate2.compareTo(dueDate1);
        }

    };
}
