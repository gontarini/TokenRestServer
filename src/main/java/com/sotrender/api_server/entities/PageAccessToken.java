package com.sotrender.api_server.entities;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Class creates page access token object with certain fields about it
 * @author pawel
 */
public class PageAccessToken {
	/**
	 * Facebook page access token
	 */
	public String pageAccessToken;

	/**
	 * Page unique identifier
	 */
	public String pageId;

	/**
	 * Page name
	 */
	public String name;
		
	/**
	 * Application identifier on which token can work
	 */
	public String appId;
	
	/**
	 * Unique mongo identifier of parent token which manages this token. Mongo collection is called by the social media name
	 */
	public String parentId;
	
	/**
	 * Available access for the parent token to that page
	 */
	public String accessLevel;
	
	public List<String> permissions;
	
	private AccessLevelWage wage;
	
	public int accessLevelWage;
	/**
	 * Constructor which invokes method to create correct object and do it for its own.
	 * @param data data from which object might be build
	 * @param appId application identifier
	 * @param parentId  parent token mongo identifier
	 */
	public PageAccessToken(JsonNode data, String appId, String parentId, List<String> permissions){
		wage = new AccessLevelWage();

		this.setPageData(data);
		this.appId = appId;
		this.parentId = parentId;
		this.permissions = permissions;
	}
	
	/**
	 * Set fields for the pageACcessToken object 
	 * @param data data to set fields up
	 */
	private void setPageData(JsonNode data){
		this.name = data.get("name").asText();
		this.pageId = data.get("id").asText();

		
		this.accessLevel = data.get("perms").get(0).toString();
		this.accessLevel = this.accessLevel.replace("\"", "");
		this.pageAccessToken = data.get("access_token").asText();

		this.accessLevelWage = wage.get(this.accessLevel);
	}
	
	/**
	 * Method sets facebook permissions
	 * @param permissions
	 */
	public void setPermissions(List<String> permissions){
		this.permissions = permissions;
	}
}
