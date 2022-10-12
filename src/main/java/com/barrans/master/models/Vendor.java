package com.barrans.master.models;


import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "vendor")
public class Vendor extends CommonObjectActiveAndCreatedDate implements Serializable {
    @Column(name = "code", length = 50, unique=true,nullable=false)
    public String code;

    @Column(name = "name", length = 255, nullable = false)
    public String name;

    @Column(name = "address", length = 1000)
    public String address;

    @Column(name = "tax_address", length = 1000)
    public String tax_address;

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

    @Column(name = "contact")
    public String contact;

    @Column(name = "email")
    public String email;

    @Column(name = "web")
    public String web;

    @Column(name = "suspended")
    public boolean suspended;

    @Column(name = "term_id")
    public int term_id;

    @Column(name = "is_taxable")
    public boolean is_taxable;

    @Column(name = "type_tax1")
    public int type_tax1;

    @Column(name = "type_tax2")
    public int type_tax2;

    @Column(name = "default_invoice")
    public boolean default_invoice;

    @Column(name = "tax_number")
    public String tax_number;

    @Column(name = "nppkp")
    public String nppkp;

    @Column(name = "currency_id")
    public int currency_id;

    @Column(name = "opening_balance")
    public double opening_balance;

    public double plafond;
    public double reminding;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "as_of", length = 29)
    public Date as_of;

    @Column(name = "note",length=1000)
    public String note;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", length = 29)
    public Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", length = 29)
    public Date updated_at;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL )
    public List<VendorContact> contacts = new ArrayList<VendorContact>();

    @ManyToOne
    public Branch branch;

    public List<VendorContact> getContacts() {
        return contacts;
    }

//    public static Page<Vendor> page(int page, int pageSize) {
//        return find.where().findPagingList(pageSize).getPage(page);
//    }
    public String action;
}
