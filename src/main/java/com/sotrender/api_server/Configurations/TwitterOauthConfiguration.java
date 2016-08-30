package com.sotrender.api_server.Configurations;

import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration class for twitter application
 * @author pawel
 *
 */
public class TwitterOauthConfiguration {

	@NotEmpty
	@JsonProperty
	private String appId;
	/**
	 * Application consumer Key 
	 */
	@NotEmpty
	@JsonProperty
	private String consumerKey;
	
	/**
	 * Consumer Secret key
	 */	
	@NotEmpty
	@JsonProperty
	private String consumerSecret;
	

	
	public String getConsumerKey(){
		return consumerKey;
	}

	public String getConsumerSecret(){
		return consumerSecret;
	}
	
	public String getAppId(){
		return appId;
	}
}
