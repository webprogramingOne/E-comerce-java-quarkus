package com.barrans.master.models;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "product")
public class Product extends CommonObjectActiveAndCreatedDate implements Serializable {
    private static final long SerialVersionUID = 1L;
    @Column(name = "name", length = 50, nullable = false)
    public String name;
    @Column(name = "description", length = 220, nullable = false)
    public String description;
    @Column(name = "price", length = 50, nullable = false)
    public Double price;
}
