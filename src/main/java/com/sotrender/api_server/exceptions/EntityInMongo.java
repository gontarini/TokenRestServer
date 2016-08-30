package com.sotrender.api_server.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.mongodb.MongoException;


/**
 * Class implements ExceptionMapper and create if given token is already in database.
 * @author pawel
 *
 */
@Provider
public class EntityInMongo implements ExceptionMapper<MongoException> {

	/**
	 * The following method is invoked if token is already stored.
	 * That gives information about it.
	 */
	public Response toResponse(MongoException exception) {
		return Response.status(200).
			      entity(exception.getMessage()).
			      type("text/plain").
			      build();
	}

	public Response toResponse(String message){
		return Response.status(404).
			      entity(message).
			      type("text/plain").
			      build();
	}
}
