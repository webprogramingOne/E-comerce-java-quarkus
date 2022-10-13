package com.barrans.master.models;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "supervisor")
public class Supervisor extends CommonObjectActiveAndCreatedDate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(length = 50, unique = true, nullable = false)
    @NotNull(message = "Please provide a address")
    public String tm_no;

    @NotNull(message = "Please provide a name")
    public String name;

    @NotNull(message = "Please provide a phone")
    public String phone;

    @NotNull(message = "Please provide a email")
    public String email;



}
