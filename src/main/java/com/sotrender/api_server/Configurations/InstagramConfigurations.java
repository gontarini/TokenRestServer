package com.sotrender.api_server.Configurations;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstagramConfigurations {

	/**
	 * Application id's
	 */	
	@NotEmpty
	@JsonProperty
	private String appId;
	
	public String getAppId(){
		return appId;
	}
}
