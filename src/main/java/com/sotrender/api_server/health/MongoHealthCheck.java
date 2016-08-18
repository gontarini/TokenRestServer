package com.sotrender.api_server.health;

import com.codahale.metrics.health.HealthCheck;
import com.sotrender.api_server.db.MongoManaged;

/**
 * Class checks connection status with mongo database
 * @author pawel
 */
public class MongoHealthCheck extends HealthCheck {
	
	/**
	 * MongoManaged instance
	 */
	private MongoManaged mongo;

	/**
	 * Constructor sets given instance to local field
	 * @param mongoManaged
	 */
    public MongoHealthCheck(MongoManaged mongoManaged) {
        this.mongo = mongoManaged;
    }

    /**
     * Method checks health of connection
     */
    @Override
    protected Result check() throws Exception {
        mongo.getDb();
        return Result.healthy();
    }
}
