package com.barrans.master.controllers;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import com.barrans.master.services.TermOfPaymentService;
import com.barrans.util.IAction;
import com.barrans.util.SimpleResponse;

@Path("/api/v1/master/vendorContact")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VendorContactController implements IAction {
	@Inject
	TermOfPaymentService service;

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

}
