package com.sotrender.api_server.core;

import org.bson.Document;

/**
 * Class creates response object for get requests for facebook pages token
 * @author pawel
 *
 */
public class GetResponsePageFacebook extends GetResponse{

	/**
	 * Mongo id of the token above this page access token
	 */
	public String parentId;
	
	/**
	 * The strongest permission which particular parent token own
	 */
	public String permissions;
	
	/**
	 * Access to page by particular parent token
	 */
	public String accessLevel;

	/**
	 * Access to page by particular parent token in integer mode
	 */
	public int accessLevelWage;
	
	/**
	 * Invoking parent constructor and retrieve certain data from document passed into constructor
	 * @param document document retrieved from mongo database
	 */
	public GetResponsePageFacebook(Document document) {
		super(document,true);
		this.parentId = document.get("parentId").toString();
		this.permissions = document.get("permissions").toString();
		this.accessLevel = document.get("accessLevel").toString();
		this.accessLevelWage = document.getInteger("accessLevelWage", 0);
		
	}

}
