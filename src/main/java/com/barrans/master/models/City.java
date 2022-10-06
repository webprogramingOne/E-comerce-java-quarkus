package com.barrans.master.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.barrans.util.CommonObjectCreatedDate;



@Entity
@Table(name="city")
public class City extends CommonObjectCreatedDate implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
    @Column(name = "code", length = 20, nullable = false)
    @NotNull(message = "Please provide a city code")
    public String code;
    
    @Column(name = "name", length = 50, nullable = false)
    @NotNull(message = "Please provide a city name")
    public String name;

    @Column(name = "initial", length = 255, nullable = false)
    @NotNull(message = "Please provide a city initial")
    public String initial;

    @ManyToOne
    public Province province;
    
}