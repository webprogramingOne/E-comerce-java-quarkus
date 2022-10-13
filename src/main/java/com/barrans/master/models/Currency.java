package com.barrans.master.models;


import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table("Currency")
public class Currency extends CommonObjectActiveAndCreatedDate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(unique = true)
    @NotNull(message = "please insert a name")
    public String name;

    public double rate;

    public double inverse_rate;
    public String country;
    @Column(unique = true)
    public String symbol;
    public boolean is_primary;
    public boolean suspended;
    public Date created_at;
    public Date updated_at;

    @ManyToOne
    public Branch branch;

//    public static Finder<Long, Currency> find = new Finder<Long, Currency>(Long.class, Currency.class);

}
