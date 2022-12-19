package com.barrans.master.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.barrans.util.CommonObjectActiveAndCreatedDate;

@Entity
@Table(name = "vendor")
public class Vendor extends CommonObjectActiveAndCreatedDate implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "code", length = 50, unique=true, nullable=false)
	public String code;

	@Column(name = "name", length = 255, nullable = false)
	public String name;

	@Column(name = "address", columnDefinition = "TEXT")
	public String address;

	@Column(name = "tax_address", columnDefinition = "TEXT")
	public String taxAddress;

	@Column(name = "city", length = 50)
	public String city;

	@Column(name = "region", length = 50)
	public String region;

	@Column(name = "zip", length = 10)
	public String zip;

	@Column(name = "country", length = 50)
	public String country;

	@Column(name = "phone")
	public String phone;

	@Column(name = "email")
	public String email;

	@Column(name = "website")
	public String website;

	@Column(name = "term_id")
	public Integer termId;
	
	@Column(name = "is_taxable")
	public Boolean isTaxable;

	@Column(name = "type_tax1")
	public Integer typeTax1;

	@Column(name = "type_tax2")	
	public Integer typeTax2;

	@Column(name = "default_invoice")
	public Boolean defaultInvoice;

	@ManyToOne
	public TaxNumber taxNumber;

	@Column(name = "nppkp")
	public String nppkp;

	@ManyToOne
	public Currency currency;

	@Column(name = "opening_balance")
	public Double openingBalance;
	
	public Double plafond;
	public Double reminding;

	@Column(name = "as_of")
	public Date asOf;

	@Column(name = "note", columnDefinition = "TEXT")
	public String note;

	@Column(name = "company_id", length = 36, nullable = false)
	@NotNull(message = "Please provide a company id")
	public String companyId;

}
