package com.sotrender.api_server.MyApplicationConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Main configuration class which creates instances of other configuration classes
 * @author pawel
 *
 */
public class MainConfiguration extends Configuration {

	/**
	 * instance of mongo configuration class
	 */
    @Valid
    @NotNull
    public MongoConfiguration mongo = new MongoConfiguration();
    
    /**
     * Instance of facebook configuration class
     */
    @Valid
    @NotNull
    public FacebookConfiguration facebook = new FacebookConfiguration();
	
    /**
     * Instance of twitter configuration class
     */
    @Valid
    @NotNull
    public TwitterOauthConfiguration twitter = new TwitterOauthConfiguration();
}

