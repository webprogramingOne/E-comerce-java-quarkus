package com.barrans.master.models;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "purchase")
public class Purchase extends CommonObjectActiveAndCreatedDate implements Serializable {
    private static final long SerialVersionUID = 1L;
    @Column(name="purchase_number")
    public String purchaseNumber;
    @Column(nullable = false)
    public Date date = new Date();
    @ManyToOne
    public Supplier supplier;
}
