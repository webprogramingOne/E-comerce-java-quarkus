package com.barrans.master.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.barrans.util.CommonObjectCreatedDate;


@Entity
@Table(name="province")
public class Province extends CommonObjectCreatedDate implements Serializable{
	private static final long serialVersionUID = 1L;
	
    @Column(name = "code", length = 20, nullable = false)
    @NotNull(message = "Please provide a province code")
    public String code;
    
    @Column(name = "name", length = 50, nullable = false)
    @NotNull(message = "Please provide a province name")
    public String name;
    
  

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}