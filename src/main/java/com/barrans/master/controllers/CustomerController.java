package com.barrans.master.controllers;

import com.barrans.master.services.CustomerService;
import com.barrans.util.IAction;
import com.barrans.util.SimpleResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/master/customer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerController {

    @Inject
    CustomerService service;

    @GET
    @Path("/testAPICustomer")
    public SimpleResponse testAPICustomer(){
        return null;
    }

    @POST
    @Path("/tets")
    public SimpleResponse insert()
}
