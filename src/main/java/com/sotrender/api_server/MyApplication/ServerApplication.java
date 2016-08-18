package com.sotrender.api_server.MyApplication;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.sotrender.api_server.db.MongoManaged;
import com.sotrender.api_server.health.MongoHealthCheck;
import com.sotrender.api_server.resources.Resources;
import com.sotrender.api_server.MyApplicationConfiguration.MainConfiguration;

/**
 * Main class of the project which loads configurations,
 * add them to enviroment and register resources class.
 * @author pawel
 */
public class ServerApplication extends Application<MainConfiguration> {
	
	/**
	 * Main method of the project which run every single thread
	 * @param args command lines argument
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {
        new ServerApplication().run(args);
    }

    /**
     * Getter name 
     */
    @Override
    public String getName() {
        return "token-sotrender";
    }

    /**
     * Overriding method which initialize configuration staff
     */
    @Override
    public void initialize(Bootstrap<MainConfiguration> bootstrap) {
        new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false)
        		);
    }

    /**
     * Run method which loads configurations and registers enviroment objects. 
     * It is also health check making inside
     */
    @Override
    public void run(MainConfiguration configuration,
                    Environment environment) throws Exception {

    	MongoManaged mongoManaged = new MongoManaged(configuration.mongo);
    	
        environment.lifecycle().manage(mongoManaged);
        environment.healthChecks().register("MongoHealthCheck", new MongoHealthCheck(mongoManaged));

        environment.jersey().register(new Resources(mongoManaged, configuration.facebook, configuration.twitter));

    }
    
    

}

