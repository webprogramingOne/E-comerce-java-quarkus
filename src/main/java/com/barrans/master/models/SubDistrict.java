package com.barrans.master.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.barrans.util.CommonObjectCreatedDate;

@Entity
@Table(name="sub_district")
public class SubDistrict extends CommonObjectCreatedDate implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
    @Column(name = "code", length = 20, nullable = false)
    @NotNull(message = "Please provide a Sub District Code")
    public String code;
    
    @Column(name = "name", length = 50, nullable = false)
    @NotNull(message = "Please provide a Sub District Name")
    public String name;

    @ManyToOne
    public District district;
}