package com.sotrender.api_server.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

public class ValidationFailure implements ExceptionMapper<ProcessingException>{

	public Response toResponse(ProcessingException exception) {
		return Response.status(404).
			      entity(exception.getMessage()).
			      type("text/plain").
			      build();
	}

}
