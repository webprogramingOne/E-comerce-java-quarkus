package com.barrans.master.controllers;

import com.barrans.master.services.CustomerService;
import com.barrans.util.IAction;
import com.barrans.util.SimpleResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/one/customer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerController implements IAction {
	@Inject
	CustomerService service;

	@POST
	@Path("/insert")
	@Override
	public SimpleResponse insert(Object param, @HeaderParam("X-Consumer-Custom-ID") String header) {
		return service.insert(param, header);
	}

	@POST
	@Path("/update")
	@Override
	public SimpleResponse update(Object param, @HeaderParam("X-Consumer-Custom-ID") String header) {
		return service.update(param, header);
	}

	@POST
	@Path("/inquiry")
	@Override
	public SimpleResponse inquiry(Object param) {
		return service.inquiry(param);
	}

	@POST
	@Path("entity")
	@Override
	public SimpleResponse entity(Object param) {
		return service.entity(param);
	}

	@POST
	@Path("/delete")
	public  SimpleResponse delete(Object param){return service.delete(param);}

}
