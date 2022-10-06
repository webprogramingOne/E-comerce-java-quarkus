package com.barrans.master.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.barrans.util.CommonObjectActiveAndCreatedDate;


@Entity
@Table(name="tax")
public class Tax  extends CommonObjectActiveAndCreatedDate implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public Type type;
	
    @Column(name = "description", length = 50, nullable = false)
    @NotNull(message = "Please provide a tax description")
	public String description;
    
    @Column(name = "rate", nullable = false)
    @NotNull(message = "Please provide a tax rate")
	public double rate;
	
	public enum Type {
		PPN,
		PPH,
		PPNBM
	}
	
}
