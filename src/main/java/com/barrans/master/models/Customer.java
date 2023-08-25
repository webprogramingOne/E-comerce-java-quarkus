package com.barrans.master.models;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
@Entity
@Table(name = "customer")
public class Customer extends CommonObjectActiveAndCreatedDate implements Serializable {
    private static final long SerialVersionUID = 1L;
    @Column(name = "name", length = 50, nullable = false)
    @NotNull(message = "Please provide a name")
    public String name;
    @Column(name = "phone", length = 20, nullable = false)
    public String phone;
    @Column(name = "address", length = 220, nullable = false)
    public String address;
}
