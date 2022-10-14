package com.barrans.master.models;


import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "currency")
public class Currency extends CommonObjectActiveAndCreatedDate implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(unique = true)
    @NotNull(message = "please insert a name")
    public String name;

    public double rate;
    
    @Column(name = "inverse_rate")
    public double inverseRate;
    public String country;
    
    @Column(unique = true)
    public String symbol;
    
    @Column(name = "is_primary")
    public boolean isPrimary;

}
