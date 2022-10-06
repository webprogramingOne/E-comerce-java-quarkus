package com.barrans.master;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/master")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApplicationResource {

	@GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Ping ping() {
    	return new Ping();
    }
}
