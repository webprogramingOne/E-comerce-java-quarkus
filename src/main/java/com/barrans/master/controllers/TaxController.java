package com.barrans.master.controllers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.barrans.master.services.TaxService;
import com.barrans.util.*;

@Path("/api/v1/master/tax")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class TaxController implements IAction {
	@Inject
	TaxService service;

	@POST
	@Path("/register")
	@Override
	public SimpleResponse insert(Object param, @HeaderParam("x-consumer-id") String header) {
		// TODO Auto-generated method stub
		return service.insert(param, header);
	}

	@Override
	public SimpleResponse update(Object param, String header) {
		// TODO Auto-generated method stub
		return null;
	}

	@POST
	@Path("/inquiry")
	@Override
	public SimpleResponse inquiry(Object param) {
		// TODO Auto-generated method stub
		return service.inquiry(param);
	}

	@GET
	@Path("/types")
	public SimpleResponse getTypes() {
		return service.getTypes();
	}

	@Override
	public SimpleResponse entity(Object param) {
		// TODO Auto-generated method stub
		return null;
	}

}
