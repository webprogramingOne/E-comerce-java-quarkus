package com.barrans.master.models;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "sales")
public class Sales extends CommonObjectActiveAndCreatedDate implements Serializable {
    private static final long SerialVersionUID = 1L;
    @Column(nullable = false)

    public Date date = new Date();
    @Column(name="transaction_number")
    public String transactionNumber;
    @ManyToOne
    public Customer customer;
}
