package com.barrans.master.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.barrans.util.CommonObjectCreatedDate;


@Entity
@Table(name = "chart_of_account_type")
public class ChartOfAccountType extends CommonObjectCreatedDate implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Column(name = "code", length = 30, nullable = false)
    @NotNull(message = "Please provide a code")
	public String code;

	@Column(name = "name", length = 100, nullable = false)
    @NotNull(message = "Please provide a name")
	public String name;
	
}
