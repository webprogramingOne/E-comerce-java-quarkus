package com.barrans.master.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

@Entity
@Table(	name = "chart_of_account", 
		uniqueConstraints = { 
				@UniqueConstraint(columnNames = { "code", "company_id" }) 
			}
		)
public class ChartOfAccount extends CommonObjectActiveAndCreatedDate implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	public ChartOfAccount parent;
	
	@ManyToOne
	public ChartOfAccountType type;

	@ManyToOne
	public Currency currency;
	
	@Column(name = "code", length = 30, nullable = false)
	public String code;
	
	@Column(name = "name", length = 100, nullable = false)
	public String name;
	
	@Column(name = "level", nullable = false)
	public Integer level;
	
	@Column(name = "sub_account", nullable = false)
	public Boolean subAccount;
	
	public Double balance;
	
	public String description;

	@Column(name = "company_id", length = 36, nullable = false)
	@NotNull(message = "Please provide a company id")
	public String companyId;
}
