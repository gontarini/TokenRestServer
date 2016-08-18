package com.sotrender.api_server.core;

import org.bson.Document;
/**
 * Class creates object for twitter token get request
 * @author pawel
 *
 */
public class GetResponseTwitter extends GetResponse{
	
	/**
	 * Constructor which invokes parent constructor with false parameter which indicate that it's not a page access token.
	 * Check documentation in parent class
	 * @param document document retrieved from mongo database
	 */
	public GetResponseTwitter(Document document) {
		super(document, false);
		this.secret = document.get("secret").toString();	
	}

	/**
	 * Twitter secret token
	 */
	public String secret;

}
