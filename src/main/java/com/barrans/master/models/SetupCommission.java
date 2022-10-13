package com.barrans.master.models;

import com.barrans.util.CommonObjectActiveAndCreatedDate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "salesman_setup_commission")
public class SetupCommission extends CommonObjectActiveAndCreatedDate implements Serializable {

    private static final long serialVersionUID = 1L;

    public Integer salescommission_id;

    @ManyToOne
    public Salesman salesman;
}
