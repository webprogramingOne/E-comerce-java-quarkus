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

import com.barrans.master.services.PostalCodeService;
import com.barrans.util.GeneralConstants;
import com.barrans.util.SimpleResponse;


@Path("/api/v1/master/postalCode")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostalCodeController {
    
    @Inject
    PostalCodeService postalCodeService;

    private final static Logger LOGGER = LoggerFactory.getLogger(PostalCodeController.class.getName());
    
    @POST
    @Path("/inquiry")
    public SimpleResponse inquiry (Object param){
        try {
            return postalCodeService.inquiry(param);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
        }

    }
    @POST
    @Path("/byId")
    public SimpleResponse entity (Object param){
        try {
            return postalCodeService.entity(param);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
        }

    }
    @POST
    @Path("/insert")
    public SimpleResponse insert (Object param, @HeaderParam("X-Consumer-Custom-ID") String header){
        try {
            return postalCodeService.insert(param, header);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
        }

    }
    @POST
    @Path("/update")
    public SimpleResponse update (Object param, @HeaderParam("X-Consumer-Custom-ID") String header){
        try {
            return postalCodeService.update(param, header);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
        }

    }
    @POST
    @Path("/delete")
    public SimpleResponse delete (Object param, @HeaderParam("X-Consumer-Custom-ID") String header){
        try {
            return postalCodeService.delete(param, header);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
        }

    }
    
}
