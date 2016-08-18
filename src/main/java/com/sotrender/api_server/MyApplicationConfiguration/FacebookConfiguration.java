package com.sotrender.api_server.MyApplicationConfiguration;

import javax.validation.constraints.NotNull;

/**
 * Class holding facebook configurations
 * @author pawel
 *
 */
public class FacebookConfiguration {
	/**
	 * Application secret token
	 */
	@NotNull
	public String secret;
	
	/**
	 * Application id
	 */
	@NotNull
	public String appId;
}
