package com.barrans.master.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.barrans.util.CommonObjectCreatedDate;

@Entity
@Table(name="customer_type")
public class CustomerType extends CommonObjectCreatedDate implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
    @Column(name = "name", length = 50, nullable = false)
    @NotNull(message = "Please provide a name")
    public String name;

}