package com.barrans.master.models;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "shipment")
public class Shipment extends CommonObjectActiveAndCreatedDate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 150, nullable = false)
    public String name;

    @Temporal(TemporalType.TIMESTAMP.TIMESTAMP)
    @Column(name = "created_at", length = 29)
    public Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", length = 29)
    public Date updated_at;

//    @ManyToOne
//    public Branch branch;

//    public static Finder<Long, Shipment> find = new Finder<Long, Shipment>(Long.class, Shipment.class);
//
//    @Transient
//    public String getCreated_at() {
//        return GeneralConfig.getInstance().getStringDate(created_at);
//    }
//
//    @Transient
//    public String getUpdated_at() {
//        return GeneralConfig.getInstance().getStringDate(updated_at);
//    }
//
//    public static Page<Shipment> page(int page, int pageSize) {
//        return find.where().findPagingList(pageSize).getPage(page);
//    }
}
