package com.barrans.master.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.barrans.util.CommonObjectActiveAndCreatedDate;

@Entity
@Table(name="tax_number")
public class TaxNumber extends CommonObjectActiveAndCreatedDate implements Serializable {

	private static final long serialVersionUID = 1L;

  	public String from;
  	public String to;
  	public String fixed;
  
	@Column(name = "company_id", length = 36, nullable = false)
	@NotNull(message = "Please provide a company id")
	public String companyId;
  
}
