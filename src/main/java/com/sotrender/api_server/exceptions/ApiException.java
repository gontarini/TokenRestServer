package com.sotrender.api_server.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ApiException extends Throwable implements ExceptionMapper<Exception>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Response toResponse(Exception exception) {
		return Response.status(404).
			      entity(exception.getMessage()).
			      type("text/plain").
			      build();
	}

	public Response toResponse(String string) {
		return Response.status(404).
			      entity(string).
			      type("text/plain").
			      build();
	}

}
