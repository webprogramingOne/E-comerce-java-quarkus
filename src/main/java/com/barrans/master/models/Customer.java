package com.barrans.master.models;


import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.*;
import javax.xml.stream.Location;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "customer")
public class Customer extends CommonObjectActiveAndCreatedDate implements Serializable {


    public String code;
    @Column(length = 50, unique = true, nullable = false)
    public String generated_code;
    @Column(length = 255, nullable = false)
    public String name;
    @Column(length = 1000)
    public String address;
    @Column(length = 1000)
    public String tax_address;
    public String city;
    public String region;
    public String zip;
    public String country;
    public String phone;
    public String contact_person;
    public String email;
    public String web;
    public boolean suspended;
    public int term_id;
    public boolean is_taxable;
    public int type_tax1;
    public int type_tax2;
    public boolean default_invoice;
    public String tax_number;
    public String nppkp;
    public long salesman_id;
    public int customer_type_id;
    public int level_price;
    public int default_discount;
    public int sales_office_id;
    public int area_id;
    public String longitude;
    public String latitude;
    public Date last_updated_longlat;
    @Column(nullable = false)
    public int currency_id;
    public double openingBalance;
    public double noOutstandingInvoiceAfter;
    public double ifCurrentOwningNotExeededThan;
    public Date as_of;
    public String note;

    @Column(length = 1000)
    public String gcm_token;
    public int stock;
    public Date created_at;
    public Date updated_at;
    public Boolean approval;

    @ManyToOne
    public Location location;

    @OneToMany
    public List<CustomerTrx> trxs = new ArrayList<CustomerTrx>();

    @ManyToOne
    public Branch branch;

}
