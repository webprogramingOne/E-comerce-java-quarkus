package com.barrans.master.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barrans.util.GeneralConstants;
import com.barrans.util.SimpleResponse;

@Provider
public class GeneralServiceExceptionHandlerProvider implements ExceptionMapper<GeneralServiceException> {

	Logger LOGGER = LoggerFactory.getLogger(GeneralServiceExceptionHandlerProvider.class);

    @Override
    public Response toResponse(GeneralServiceException exception) {
    	LOGGER.error("GeneralTaskExceptionHandlerProvider: " + exception.getMessage(), exception);
    	
    	SimpleResponse response = new SimpleResponse(GeneralConstants.FAIL_CODE, GeneralConstants.FAILED, exception.getMessage());
    		
       return Response.status(Status.BAD_REQUEST).entity(response).build();
    }
}
