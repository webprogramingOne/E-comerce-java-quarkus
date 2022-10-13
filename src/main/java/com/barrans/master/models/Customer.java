package com.barrans.master.models;


import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.stream.Location;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "customer")
public class Customer extends CommonObjectActiveAndCreatedDate implements Serializable {

    private static final long serialVersionUID = 1L;

    // addrees
    public String code;

    @Column(length = 50, unique = true, nullable = false)
    @NotNull(message = "Please provide a city code")
    public String generated_code;

    @Column(length = 255, nullable = false)
    @NotNull(message = "Please provide a name code")
    public String name;

    @Column(length = 1000)
    @NotNull(message = "Please provide a address code")
    public String address;

    // bingung
    @Column(length = 1000)
    @NotNull(message = "Please provide a address code")
    public String tax_address;

    @NotNull(message = "Please provide a address code")
    public String city;

    @NotNull(message = "Please provide a address code")
    public String region;

    @NotNull(message = "Please provide a address code")
    public String zip;

    @NotNull(message = "Please provide a address code")
    public String country;

    @NotNull(message = "Please provide a address code")
    public String phone;

    @NotNull(message = "Please provide a address code")
    public String contact_person;

    @NotNull(message = "Please provide a address code")
    public String email;

    public String web;

    @NotNull(message = "Please provide a address code")
    public String longitude;

    @NotNull(message = "Please provide a address code")
    public String latitude;

    @NotNull(message = "Please provide a address code")
    public Date last_updated_longlat;

    // bingung
    public boolean suspended;

    // terms
    public int term_id;

    public double noOutstandingInvoiceAfter;

    public double ifCurrentOwningNotExeededThan;

    // taxes
    public boolean is_taxable;

    public int type_tax1;

    public int type_tax2;

    public boolean default_invoice;

    public String tax_number;

    public String nppkp;

    // sales
    @NotNull(message = "Please provide a address code")
    public long salesman_id;

    @NotNull(message = "Please provide a address code")
    public int customer_type_id;

    @NotNull(message = "Please provide a address code")
    public int level_price;

    @NotNull(message = "Please provide a address code")
    public int default_discount;

    @NotNull(message = "Please provide a address code")
    public int sales_office_id;

    @NotNull(message = "Please provide a address code")
    public int area_id;

    // balance
    @Column(nullable = false)
    public int currency_id;

    public double openingBalance;

    public Date as_of;

    // note
    public String note;

    @Column(length = 1000)
    public String gcm_token;

    public int stock;

    public Date created_at;

    public Date updated_at;

    public Boolean approval;

    @ManyToOne
    @NotNull(message = "Please provide a address code")
    public Location location;

//    @OneToMany
//    public List<CustomerTrx> trxs = new ArrayList<CustomerTrx>();
//
//    @ManyToOne
//    public Branch branch;

}
