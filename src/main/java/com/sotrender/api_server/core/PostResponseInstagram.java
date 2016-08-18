package com.sotrender.api_server.core;

import org.bson.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.sotrender.api_server.exceptions.TokenExpired;

/**
 * Class extends PostResponse class with range of instagram token things.
 * @author pawel
 */
public class PostResponseInstagram extends PostResponse{
	
	/**
	 * Instagram access token
	 */
	private String accessToken;
	
	/**
	 * Constructor which invokes parent constructor.
	 * @param doc document retrieved from mongo database
	 */
	public PostResponseInstagram(Document doc){
		super(doc);
	}

	/**
	 * Overriding method which create response object basing on given mongo document 
	 * and invoke checkToken method
	 */
	@Override
	public void createEntity() throws TokenExpired {
		this.mongoId = this.doc.get("_id").toString();
		this.appId = this.doc.get("app_id").toString();
		
		this.token = this.doc.get("token").toString();

//		this.accessToken = "3079523915.1677ed0.bc3d9dfc74fb4742996e7f2e61a27501";
		this.accessToken = this.token;
		checkToken();
	}

	/**
	 * Overriding method to make get request to instagram api
	 * and retrieve certain data about specified user in such access token
	 */
	@Override
	protected void checkToken() throws TokenExpired {
		String url = "https://api.instagram.com/v1/users/self/?access_token=" + this.accessToken;
		
		JsonNode instagramJson = makeRequest(url);
		JsonNode dataInstagram = instagramJson.get("data");
		JsonNode metaInstagram = instagramJson.get("meta");
		
		if (metaInstagram.get("code").asInt() == 200){
			System.out.println("requestDone");
			this.userId = dataInstagram.get("id").asLong();
			System.out.println("id");
			this.picture = dataInstagram.get("profile_picture").asText();
			System.out.println("picture");
			this.name = dataInstagram.get("full_name").asText();
		}
		else{
			throw new TokenExpired();
		}
	}
}
