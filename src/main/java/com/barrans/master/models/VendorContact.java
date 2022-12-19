package com.barrans.master.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.barrans.util.CommonObjectActiveAndCreatedDate;

@Entity
@Table(name="vendor_contact")
public class VendorContact extends CommonObjectActiveAndCreatedDate implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "first_name", length = 30 )
	public String firstName;

	@Column(name = "last_name", length = 30 )
	public String lastName;

	@Column(name = "job_title", length = 30 )
	public String jobTitle;
	
	@Column(name = "phone_office", length = 20 )
	public String phoneOffice;
	
	@Column(name = "ext", length = 5 )
	public String ext;
	
	@Column(name = "mobile", length = 15 )
	public String mobile;

	@Column(name = "fax", length = 20 )
	public String fax;

	@Column(name = "email", length = 30 )
	public String email;
	
	@ManyToOne
	public Vendor vendor;
}
