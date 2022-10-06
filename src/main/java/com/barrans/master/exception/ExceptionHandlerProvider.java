package com.barrans.master.exception;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.barrans.util.GeneralConstants;
import com.barrans.util.SimpleResponse;

@Provider
public class ExceptionHandlerProvider implements ExceptionMapper<Exception> {

	Logger log = LoggerFactory.getLogger(ExceptionHandlerProvider.class);

	private ObjectMapper om = new ObjectMapper();

    @Override
    public Response toResponse(Exception exception) {
    	log.error("ExceptionHandler", exception);

    	Object payload;
    	try {
            Map<String, Object> message = om.convertValue(exception.getMessage(), new TypeReference<Map<String,Object>>(){});
    	    payload = message;
        } catch (Exception e) {
    	    payload = exception.getMessage();
        }
    	
    	SimpleResponse response = new SimpleResponse(GeneralConstants.FAIL_CODE, GeneralConstants.FAILED, payload);
    	
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }
}
