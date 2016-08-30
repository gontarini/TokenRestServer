package com.sotrender.api_server.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;

/**
 * Exception class implements ExceptionMapper interface.
 * Basically exception of that class is thrown whether parsing exception occur.
 * @author pawel
 */
@Provider
public class JsonExceptionMapper implements ExceptionMapper<JsonParseException> {
	
	/**
	 * Creates Response object with status 404 which indicates failure operation and send concrete message about it
	 */
	public Response toResponse(JsonParseException exception) {
		return Response.status(404).
			      entity(exception.getMessage()).
			      type("text/plain").
			      build();
	}
}
