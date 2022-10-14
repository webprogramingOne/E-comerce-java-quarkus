package com.barrans.master.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.barrans.util.CommonObjectCreatedDate;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="postal_code")
@Setter
@Getter
public class PostalCode extends CommonObjectCreatedDate implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
    @Column(name = "code", length = 10, nullable = false)
    @NotNull(message = "Please provide a postal code")
    public String code;

    @ManyToOne
    public SubDistrict district;

}