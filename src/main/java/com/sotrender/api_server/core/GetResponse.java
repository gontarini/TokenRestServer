package com.sotrender.api_server.core;


import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongojack.Id;

/**
 * Parent class for creating response to get requests.
 * @author pawel
 */
public class GetResponse {
	/**
	 * internal object identifier in mongo database
	 */
	@Id
	public String mongoId;

	/**
	 * Access token
	 */
	public String token;

	/**
	 * Application identifier
	 */
	public String appId;

	/**
	 * Creation time in mongo database
	 */
	public String addedAt;

	/**
	 * Document retrieved from mongo database
	 */
	private Document document;

	/**
	 * Unix time indication
	 */
	private int timeStamp;

	/**
	 * Decide whether response is for pages or normal tokens,
	 * invoke main class method
	 * @param document retrieved document from database
	 * @param page parameter indicates whether response is for pages (value should be true) or for for normal token (false)
	 */
	public GetResponse(Document document, boolean page) {
		this.document = document;
		createEntity(page);
		
	}

	/**
	 * Creates response object
	 * @param page parameter indicates whether response is for pages (value should be true) or for for normal token (false)
	 */
	protected void createEntity(boolean page) {
		if (page == false) {
			this.token = this.document.get("token").toString();
			this.appId = this.document.get("app_id").toString();
			System.out.println("Im here");
		} else {
			this.token = this.document.get("pageAccessToken").toString();
			this.appId = this.document.get("appId").toString();
		}
		this.mongoId = this.document.get("_id").toString();
		
		ObjectId objId = (ObjectId) this.document.get("_id");
		this.timeStamp = objId.getTimestamp();
		this.addedAt = new java.util.Date((long) this.timeStamp * 1000)
				.toString();
		System.out.println(this.addedAt);
	}
}
