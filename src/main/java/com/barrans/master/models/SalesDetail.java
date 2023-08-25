package com.barrans.master.models;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "sales_detail")
public class SalesDetail extends CommonObjectActiveAndCreatedDate implements Serializable {
    private static final long SerialVersionUID = 1L;
    @ManyToOne
    public Product product;
    public Integer quantity;
    @ManyToOne
    public Sales sales;
}
