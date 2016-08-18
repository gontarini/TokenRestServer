package com.sotrender.api_server.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;


import org.apache.http.HttpException;

import twitter4j.TwitterException;


/**
 * Class extends Throwable which is thrown if token got expired.
 * @author pawel
 */
@SuppressWarnings("serial")
public class TokenExpired extends Throwable implements ExceptionMapper<HttpException>{
	/**
	 * Method build a response with failure status 404 and give certain info about it
	 * @param twEx Twitter exception instance
	 * @return Response object
	 */
	public Response toResponse(TwitterException twEx) {
		return Response.status(404).
			      entity(twEx.getErrorMessage()).
			      type("text/plain").
			      build();
	}
	
	/**
	 * Method build a response with failure status 404 and give certain info about it
	 * @param exception HttpException exception instance
	 * @return Response object
	 */
	public Response toResponse(HttpException exception) {
		return Response.status(404).
			      entity(exception.getMessage()).
			      type("text/plain").
			      build();
	}

}
