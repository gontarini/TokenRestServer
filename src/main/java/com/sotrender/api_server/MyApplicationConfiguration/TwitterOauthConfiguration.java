package com.sotrender.api_server.MyApplicationConfiguration;

import javax.validation.constraints.NotNull;

/**
 * Configuration class for twitter application
 * @author pawel
 *
 */
public class TwitterOauthConfiguration {
	/**
	 * Consumer key
	 */
	@NotNull
	public String consumerKey;
	
	
	/**
	 * Consumer secret key
	 */
	@NotNull
	public String consumerSecret;
	
}
