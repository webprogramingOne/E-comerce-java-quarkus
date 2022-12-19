package com.barrans.master.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.barrans.util.CommonObjectActiveAndCreatedDate;


@Entity
@Table(name="salesman")
public class Salesman extends CommonObjectActiveAndCreatedDate implements Serializable{
	private static final long serialVersionUID = 1L;
	
    @Column(name = "company_id", length = 36, nullable = false)
    @NotNull(message = "Please provide a company id")
    public String companyId;

    @Column(name = "code", length = 30, nullable = false, unique = true)
    @NotNull(message = "Please provide a salesman code")
    public String code;
    
    @Column(name = "first_name", length = 30, nullable = false)
    @NotNull(message = "Please provide a first name")
    public String firstName;
    
    @Column(name = "last_name", length = 30)
    @NotNull(message = "Please provide a last name")
    public String lastName;
    
    @Column(name = "email", length = 50, nullable = false)
    @NotNull(message = "Please provide a email")
    public String email;
    
    @Column(name = "phone_number", length = 15, nullable = false)
    @NotNull(message = "Please provide a phone number")
    public String phoneNumber;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Please provide a address")
    public String address;
    
    @Column(name = "province_id", nullable = false, length = 36)
    @NotNull(message = "Please provide a province id")
    public String provinceId;
    
    @Column(name = "city_id", nullable = false, length = 36)
    @NotNull(message = "Please provide a city id")
    public String cityId;
    
    @Column(name = "password", length = 32, nullable = false)
	public String password;
    
    @Column(name = "photo_url")
	public String photoUrl;
    
    public Double balance;
    
    @Column(name = "job_title", length = 32)
    public String jobTitle;
    
    
}
