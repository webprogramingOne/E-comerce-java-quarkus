package com.barrans.master.controllers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barrans.master.services.ProvinceService;
import com.barrans.util.GeneralConstants;
import com.barrans.util.IAction;
import com.barrans.util.SimpleResponse;


@Path("api/v1/master/province")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class ProvinceController implements IAction{

	@Inject
	ProvinceService service;

	private final static Logger LOGGER = LoggerFactory.getLogger(ProvinceController.class.getName());

	@POST
	@Path("/insert")
	public SimpleResponse insert(Object param, @HeaderParam("X-Consumer-Custom-ID") String header) {
		try {
			return service.insert(param, header);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
		
	}

	@POST
	@Path("/update")
	public SimpleResponse update(Object param, @HeaderParam("X-Consumer-Custom-ID") String header) {
		try {
			return service.update(param, header);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}

	@POST
	@Path("/inquiry")
	public SimpleResponse inquiry(Object param) {
		try {
			return service.inquiry(param);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}

	@POST
	@Path("/byId")
	public SimpleResponse entity(Object param) {
		try {
			return service.entity(param);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}

	@POST
	@Path("/delete")
	public SimpleResponse delete(Object param){
		try {
			return service.delete(param);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}

}
