package com.barrans.master.controllers;

import com.barrans.master.services.DistrictService;
import com.barrans.util.DateUtil;
import com.barrans.util.GeneralConstants;
import com.barrans.util.IAction;
import com.barrans.util.SimpleResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;


@Path("/api/v1/master/district")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DistrictController implements IAction {

    @Inject
    DistrictService service;

    @GET
    @Path("/tesAPICity")
    public SimpleResponse tesAPICity() {
        LocalDateTime date = DateUtil.now();
        return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, "API district berhasil : " + date);
    }

    @POST
    @Path("/register")
    @Override
    public SimpleResponse insert(Object param,@HeaderParam("x-consumer-id") String header) {
        return service.insert(param, header);
    }

    @POST
    @Path("/update")
    @Override
    public SimpleResponse update(Object param,@HeaderParam("x-consumer-id") String header) {
        return service.update(param, header);
    }

    @POST
    @Path("/inquiry")
    @Override
    public SimpleResponse inquiry(Object param) {
        return service.inquiry(param);
    }

    @POST
    @Path("/byId")
    @Override
    public SimpleResponse entity(Object param) {
        return service.entity(param);
    }

    @POST
    @Path("/delete")
    public SimpleResponse delete(Object param) {
        return service.delete(param);
    }

}
