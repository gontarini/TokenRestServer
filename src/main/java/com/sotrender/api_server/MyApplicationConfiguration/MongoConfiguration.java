package com.sotrender.api_server.MyApplicationConfiguration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Configuration class for mongo database connection
 * @author pawel
 */
public class MongoConfiguration {

	/**
	 * Host name 
	 */
    @NotNull
    public String host;

    /**
     * Port number
     */
    @Min(1)
    @Max(65535)
    public int port;

    /**
     * Database name
     */
    @NotNull
    public String db;

    /**
     * User namer
     */
    @NotNull
    public String user;

    /**
     * User password
     */
    @NotNull
    public String password;
}
