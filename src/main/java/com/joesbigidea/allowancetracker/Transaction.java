package com.joesbigidea.allowancetracker;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by joe on 2/13/16.
 */

@Entity
public class Transaction {
    private BigDecimal amount;
    private String description;
    private long id;
    private Date postedDate;
    private boolean allowance;

    public static Transaction allowance(Date postedDate) {
        Transaction allowance = new Transaction(new BigDecimal("5"), "Allowance");
        allowance.setAllowance(true);
        allowance.setPostedDate(postedDate);
        return allowance;
    }

    public Transaction() {
        //container
    }

    public Transaction(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
        this.postedDate = new Date();
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public boolean isAllowance() {
        return allowance;
    }

    public void setAllowance(boolean allowance) {
        this.allowance = allowance;
    }

    @Override
    public String toString() {
        return "ID: " + id + " Amount: " + amount + " Description: " + description + " Date: " + postedDate + " Allowance: " + allowance;
    }
}
