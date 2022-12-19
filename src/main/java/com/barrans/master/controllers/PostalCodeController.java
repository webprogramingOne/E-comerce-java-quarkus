package com.barrans.master.controllers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.barrans.master.services.PostalCodeService;
import com.barrans.util.SimpleResponse;

@Path("/api/v1/master/postalCode")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostalCodeController {

	@Inject
	PostalCodeService postalCodeService;

	@POST
	@Path("/inquiry")
	public SimpleResponse inquiry(Object param) {
		return postalCodeService.inquiry(param);
	}

	@POST
	@Path("/entity")
	public SimpleResponse entity(Object param) {
		return postalCodeService.entity(param);
	}

	@POST
	@Path("/insert")
	public SimpleResponse insert(Object param, @HeaderParam("X-Consumer-Custom-ID") String header) {
		return postalCodeService.insert(param, header);
	}

	@POST
	@Path("/update")
	public SimpleResponse update(Object param, @HeaderParam("X-Consumer-Custom-ID") String header) {
		return postalCodeService.update(param, header);
	}
}
