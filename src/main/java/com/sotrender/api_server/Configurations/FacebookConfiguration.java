package com.sotrender.api_server.Configurations;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class holding facebook configurations
 * @author pawel
 *
 */
public class FacebookConfiguration {
	/**
	 * Application secret token
	 */
	@NotEmpty
	@JsonProperty
	private String secret;
	
	/**
	 * Application id's
	 */	
	@NotEmpty
	@JsonProperty
	private String appId;
	
	public String getAppId(){
		return appId;
	}

	public String getSecret(){
		return secret;
	}
}
