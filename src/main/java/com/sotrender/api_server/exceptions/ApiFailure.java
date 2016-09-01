package com.sotrender.api_server.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ApiFailure implements ExceptionMapper<Exception>{

	@Override
	public Response toResponse(Exception exception) {
		return Response.status(404).
			      entity(exception.getMessage()).
			      type("text/plain").
			      build();
	}

}
