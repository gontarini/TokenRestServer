package com.sotrender.api_server.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bson.Document;
import org.mongojack.Id;

import twitter4j.TwitterException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotrender.api_server.exceptions.TokenExpired;

/**
 * Abstract class which declare method implements in classes implementing response objects.
 * 
 * @author pawel
 *
 */
public abstract class PostResponse {
	/**
	 * internal parameter for each document in mongoDB
	 */
	@Id
	public String mongoId;

	/**
	 * Application unique number
	 */
	public String appId;

	/**
	 * User unique identifier
	 */
	public long userId;

	/**
	 * Name of token owner
	 */
	public String name;

	/**
	 * URI consisting picture of token owner
	 */
	public String picture;

	/**
	 * Particular document from mongoDB
	 */
	protected Document doc;

	/**
	 * Access token
	 */
	protected String token;

	/**
	 * Parameter tells whether token is still valid
	 */
	protected boolean isValid;

	/**
	 * Executable constructor
	 * 
	 * @param doc
	 *            specified document from mongoDB
	 */
	protected PostResponse(Document doc) {
		this.doc = doc;
	}

	/**
	 * Abstract method aims to create an object full of data to make a response
	 * 
	 * @throws TokenExpired
	 * @throws TwitterException 
	 */
	public abstract void createEntity() throws TokenExpired, TwitterException;

	/**
	 * Abstract method to retrieve certain data such as: information about given
	 * token (valid_until, permissions, accounts available to contribute to),
	 * information about the user of given token.
	 * @throws TwitterException 
	 */
	protected abstract void checkToken() throws TokenExpired, TwitterException;

	
	/**
	 * Create http get request to the given url and return response as a jsonNode.
	 * @param Url url to make request on
	 * @return responded json document
	 */
	protected JsonNode makeRequest(String Url) {
		try {
			URL url = new URL(Url);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();

			BufferedReader read = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String messageToParse = "";
			String line = "";
			while ((line = read.readLine()) != null) {
				messageToParse += line;
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(messageToParse);

			return json;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
