package com.sotrender.api_server.Configurations;

import javax.validation.constraints.NotNull;

/**
 * Configuration info class holds path to schema files
 * @author pawel
 *
 */
public class Schema {

	/**
	 * Schema json for instagram and facebook 
	 */
    @NotNull
    public String faceInsta;
    
    /**
     * Schema json for twitter 
     */
    @NotNull
    public String twitter;

}
