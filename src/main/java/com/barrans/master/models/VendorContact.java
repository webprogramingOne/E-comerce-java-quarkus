package com.barrans.master.models;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="vendor_contact")
public class VendorContact extends CommonObjectActiveAndCreatedDate implements Serializable {

    private static final long serialVersionUID = 1L;

    public String first_name;

    public String last_name;

    public String job_title;

    @Column(name = "phone_office", length = 30 )
    public String phone_office;
    @Column(name = "ext", length = 10 )
    public String ext;
    public String phone_cellular;
    public String fax;
    public String email_contact;

    @ManyToOne
    public Vendor vendor;


}
