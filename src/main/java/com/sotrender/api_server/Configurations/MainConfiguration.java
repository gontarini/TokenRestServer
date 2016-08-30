package com.sotrender.api_server.Configurations;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

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
    @NotEmpty
    @JsonProperty
    private List<FacebookConfiguration> facebook;

    public List<FacebookConfiguration> getFacebookApps() {
        return facebook;
    }
	
    
    /**
     * List of twitter instances of configuration class
     */
    @NotEmpty
    @JsonProperty
    private List<TwitterOauthConfiguration> twitter;

    public List<TwitterOauthConfiguration> getTwitterApps() {
        return twitter;
    }
    
    @Valid
    @NotNull
    public Schema schema = new Schema();
    
    /**
     * List of twitter instances of configuration class
     */
    @NotEmpty
    @JsonProperty
    private List<InstagramConfigurations> instagram;

    public List<InstagramConfigurations> getInstagramApps() {
        return instagram;
    }
    
}

