package com.barrans.master.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.barrans.util.CommonObjectCreatedDate;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="province")
@Setter
@Getter
public class Province extends CommonObjectCreatedDate implements Serializable{
	private static final long serialVersionUID = 1L;
	
    @Column(name = "code", length = 20, nullable = false)
    @NotNull(message = "Please provide a province code")
    public String code;
    
    @Column(name = "name", length = 50, nullable = false)
    @NotNull(message = "Please provide a province name")
    public String name;
  
}