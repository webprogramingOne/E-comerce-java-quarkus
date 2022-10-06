package com.barrans.master.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.barrans.util.CommonObjectCreatedDate;

@Entity
@Table(name="district")
public class District extends CommonObjectCreatedDate implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
    @Column(name = "code", length = 20, nullable = false)
    @NotNull(message = "Please provide a district code")
    public String code;
    
    @Column(name = "name", length = 50, nullable = false)
    @NotNull(message = "Please provide a district name")
    public String name;

    @ManyToOne
    public City city;

}