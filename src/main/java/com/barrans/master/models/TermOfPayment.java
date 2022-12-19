package com.barrans.master.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.barrans.util.CommonObjectActiveAndCreatedDate;



@Entity
@Table(name = "term_of_payment")
public class TermOfPayment extends CommonObjectActiveAndCreatedDate implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "discount_days")
	public Integer discountDays;

	@Column(name = "discount")
	public Integer discount;

	@Column(name = "net_due_in")
	public Integer netDueIn;

	@Column(name = "description", length = 255)
	public String description;
	
    @Column(name = "company_id", length = 36, nullable = false)
    @NotNull(message = "Please provide a company id")
    public String companyId;
	
}