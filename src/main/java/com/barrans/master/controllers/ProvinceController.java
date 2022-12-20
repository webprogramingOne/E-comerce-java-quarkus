package com.barrans.master.controllers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.barrans.master.services.ProvinceService;
import com.barrans.util.IAction;
import com.barrans.util.SimpleResponse;


@Path("api/v1/master/province")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class ProvinceController implements IAction{

	@Inject
	ProvinceService service;

	@POST
	@Path("/insert")
	public SimpleResponse insert(Object param, @HeaderParam("X-Consumer-Custom-ID") String header) {
		return service.insert(param, header);
	}

	@POST
	@Path("/update")
	public SimpleResponse update(Object param, @HeaderParam("X-Consumer-Custom-ID") String header) {
		return service.update(param, header);
	}

	@POST
	@Path("/inquiry")
	public SimpleResponse inquiry(Object param) {
		return service.inquiry(param);
	}

	@POST
	@Path("/entity")
	public SimpleResponse entity(Object param) {
		return service.entity(param);
	}

	@POST
	@Path("/delete")
	public SimpleResponse delete(Object param){return service.delete(param);}

}
