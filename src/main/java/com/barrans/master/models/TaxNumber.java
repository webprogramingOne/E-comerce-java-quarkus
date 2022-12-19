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

	@Column(name = "tn_from", length = 100, nullable = false)
  	public String tnFrom;
  	
	@Column(name = "tn_to", length = 100, nullable = false)
  	public String tnTo;
  	
  	public String fixed;
  
	@Column(name = "company_id", length = 36, nullable = false)
	@NotNull(message = "Please provide a company id")
	public String companyId;
  
}
