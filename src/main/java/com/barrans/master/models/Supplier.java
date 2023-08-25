package com.barrans.master.models;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "supplier")
public class Supplier extends CommonObjectActiveAndCreatedDate implements Serializable {
    private static final long SerialVersionUID = 1L;
    @Column(name = "name", length = 50, nullable = false)
    public String name;
    @Column(name = "address", length = 220, nullable = false)
    public String address;
    @Column(name = "phone", length = 50, nullable = false)
    public String phone;
    @Column(name = "mobile", length = 50, nullable = false)
    public String mobile;
}
