package com.barrans.master.models;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "customer_type")
public class CustomerType extends CommonObjectActiveAndCreatedDate implements Serializable {

    @Column(name="name",length=100, nullable = false)
    public String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", length = 29)
    public Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", length = 29)
    public Date updated_at;

    @ManyToOne
    public Branch branch;

//    public static Finder<Long, CustomerType> find = new Finder<Long, CustomerType>(Long.class, CustomerType.class);

//    public static Page<CustomerType> page(int page, int pageSize) {
//        return find.where().findPagingList(pageSize).getPage(page);
//    }
//
//    public static Map<Integer, String> options() {
//        HashMap<Integer, String> options = new HashMap<Integer, String>();
//        options.put(0, "Choose customer type");
//        for (CustomerType c : CustomerType.find.orderBy("name").findList()) {
//            options.put(c.id, c.name);
//        }
//        return options;
//    }
}
