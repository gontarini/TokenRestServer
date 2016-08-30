package com.sotrender.api_server.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.client.MongoCollection;
import com.restfb.DefaultFacebookClient;
import com.restfb.scope.ExtendedPermissions;
import com.restfb.scope.ScopeBuilder;
import com.sotrender.api_server.Configurations.FacebookConfiguration;
import com.sotrender.api_server.db.MongoManaged;
import com.sotrender.api_server.entities.PageAccessToken;
import com.sotrender.api_server.exceptions.TokenExpired;

import org.apache.commons.lang3.time.DateUtils;
import org.bson.Document;

/**
 * Class extends PostResponse class with range of facebook token things.
 * @author pawel
 */
public class PostResponseFacebook extends PostResponse {

	/**
	 * App access token
	 */
	private String appToken;
	
	/**
	 * User access token
	 */
	private String userAccessToken;
	
	/**
	 * Time to token get expired
	 */
	public Date validUntil;

	/**
	 * Configurations of facebook, such as secret app tokens
	 */
	private static FacebookConfiguration config;

	/**
	 * Array of permissions for particular user
	 */
	public ArrayList<String> permissions;

	/**
	 * Array of page access tokens
	 */
	private ArrayList<PageAccessToken> pageAccessTokens;

	/**
	 * Array of available pages to be managed
	 */
	public ArrayList<String> accounts;


	/**
	 * Invoke parent constructor and initialize class arrays
	 * @param doc document to be inserted into mongoDB
	 */
	public PostResponseFacebook(Document doc) {
		super(doc);
		this.permissions = new ArrayList<String>();
		this.pageAccessTokens = new ArrayList<PageAccessToken>();
		this.accounts = new ArrayList<String>();
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

//		this.generateAppAccessToken(this.appId);
		this.userAccessToken = this.token;
		checkToken();
	}

	/**
	 * Overriding method to make get request to facebook api
	 * and retrieve certain data about specified user in such access token, access token info about itself
	 * and available page accounts
	 */
	@Override
	protected void checkToken() throws TokenExpired {
		String UrlDebug = "https://graph.facebook.com/v2.6/debug_token?input_token="
				+ this.userAccessToken + "&access_token=" + this.appToken;

		String UrlMe = "https://graph.facebook.com/v2.7/me?fields=id,name,picture&access_token="
				+ this.userAccessToken;

		// change token to page access token
		String UrlAccounts = "https://graph.facebook.com/v2.7/me/accounts?access_token="
				+ this.userAccessToken;

		System.out.println(UrlAccounts);
		
		this.debugToken(UrlDebug);

		this.meInfo(UrlMe);

		String permissionsUrl = "https://graph.facebook.com/v2.7/"
				+ this.userId + "/permissions?input_token=" + userAccessToken
				+ "&access_token=" + appToken;

		this.permissionsInfo(permissionsUrl);

		this.accountsInfo(UrlAccounts);

	}
	
	/**
	 * Makes request to facebook api by specified url adress and check whether token is still valid,
	 * if not throw an expire exception
	 * @param urlDebug specified url ot facebook api
	 * @throws TokenExpired exception thrown if token got expired
	 */
	private void debugToken(String urlDebug) throws TokenExpired{
		JsonNode debugToken = makeRequest(urlDebug);
		this.isValid = debugToken.get("data").get("is_valid").asBoolean();

		long secondsToExpire = debugToken.get("data").get("expires_at")
				.asLong();

		Date time = Calendar.getInstance().getTime();
		this.validUntil = DateUtils
				.addMilliseconds(time, (int) secondsToExpire);

		if (this.isValid == false) {
			throw new TokenExpired();
		}
	}

	/**
	 * Makes request to facebook api by specified url adress and get info about user
	 * @param urlMe specified url ot facebook api
	 */
	private void meInfo(String urlMe){
		JsonNode meInfo = makeRequest(urlMe);

		this.userId = meInfo.get("id").asLong();
		this.picture = meInfo.get("picture").asText();
		this.name = meInfo.get("name").asText();
	}

	/**
	 * Makes request to facebook api by specified url adress and get its permissions
	 * @param urlPermissions specified url ot facebook api
	 */
	private void permissionsInfo(String urlPermissions){
		JsonNode permissions = makeRequest(urlPermissions);
		ArrayNode permArray = (ArrayNode) permissions.get("data");

		for (final JsonNode element : permArray) {
			// System.out.println(element.get("permission").asText());
			this.permissions.add(element.get("permission").asText());
		}
	}
	
	/**
	 * Makes request to facebook api by specified url adress and get its controlled pages info, 
	 * including page access tokens, name of the page, permission for particular one
	 * @param urlAccounts urlPermissions specified url ot facebook api
	 */
	private void accountsInfo(String urlAccounts){
		JsonNode accounts = makeRequest(urlAccounts);
		ArrayNode accArray = (ArrayNode) accounts.get("data");

		for (final JsonNode element : accArray) {
			// System.out.println(element.get("name").asText());
			this.accounts.add(element.get("name").asText());
			this.pageAccessTokens.add(new PageAccessToken(element, this.appId, this.mongoId, this.permissions));
		}
		
		
	}
	
	/**
	 * Makes request to facebook api in usage of restfb module in order to receive unique app access token.
	 * Secret app token is read from configuration file.
	 * @param appId aplication id given in post message
	 */
	private void generateAppAccessToken(String appId, String appSecret) {
		com.restfb.FacebookClient.AccessToken appAccessToken = new DefaultFacebookClient()
				.obtainAppAccessToken(appId, appSecret);

		this.appToken = appAccessToken.getAccessToken();
	}

	/**
	 * Pass facebook configurations.
	 * @param configuration configuration object
	 */
	public void setUpConfiguration(String appId, String appSecret) {
		this.generateAppAccessToken(appId, appSecret);
	}

	/**
	 * Insert previously created pageAccessToken objects into mongo DB.
	 * This method should be invoked after whole process of making response to user.
	 * @param mongo parameter to give controll of db
	 * @throws JsonProcessingException error during parsing to json
	 */
	public void addPageTokensToMongo(MongoManaged mongo)
			throws JsonProcessingException {
		MongoCollection collection = mongo.getDb().getCollection(
				"facebookPages");

		ObjectMapper mapper = new ObjectMapper();

		for (PageAccessToken element : pageAccessTokens) {
			try {
				String jsonInString = "";

				// Convert object to JSON string and pretty print
				jsonInString = mapper.writerWithDefaultPrettyPrinter()
						.writeValueAsString(element);

				Document doc = mapper.readValue(jsonInString, Document.class);
				doc.append("usageCount", 1);

				collection.insertOne(doc);

			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
