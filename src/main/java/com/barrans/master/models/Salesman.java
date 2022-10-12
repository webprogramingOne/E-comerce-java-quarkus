package com.barrans.master.models;


import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "salesman")
public class Salesman extends CommonObjectActiveAndCreatedDate implements Serializable {

    public String code;
    @Column(length = 50, unique = true, nullable = false)
    public String generated_code;
    public String first_name;
    public String last_name;
    public String job_title;
    public String bussiness1;
    public String ext1;
    public String bussiness2;
    public String ext2;
    public String cellular;
    public String fax;
    @Column(unique = true)
    public String email;
    public String notes;
    public String gcm_token;
    public boolean suspended;
    public int sales_office_id;
//    @JsonIgnore
    public String password;
    public Date created_at;
    public Date updated_at;
    public String salesman_code;
    public int balance;
    public String photo_url;

    @ManyToOne
    public Branch branch;

    @OneToMany(mappedBy = "salesman", cascade = CascadeType.ALL)
    public List<SetupCommission> commissions = new ArrayList<SetupCommission>();

    public List<Location> locations  = new ArrayList<Location>();

    @OneToMany(mappedBy = "salesman", cascade = CascadeType.ALL)
    public List<AreaSalesmanAssignment> assignments = new ArrayList<AreaSalesmanAssignment>();

    public List<Customer> customers = new ArrayList<>();

//    public static Finder<Long, Salesman> find = new Finder<Long, Salesman>(Long.class, Salesman.class);
//
//    public static Page<Salesman> page(int page, int pageSize) {
//        return find.where().findPagingList(pageSize).getPage(page);
//    }




}
