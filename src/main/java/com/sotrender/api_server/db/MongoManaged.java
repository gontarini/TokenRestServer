package com.sotrender.api_server.db;

import org.bson.Document;

import io.dropwizard.lifecycle.Managed;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.MongoClientURI;

import com.sotrender.api_server.Configurations.MongoConfiguration;

/**
 * Class manages connection with mongo database and store some basic info about
 * @author pawel
 */
public class MongoManaged implements Managed {

		/**
		 * Mongo client instance
		 */
		private MongoClient mongoClient;
		
		/**
		 * Mongo Database instance
		 */
		private MongoDatabase db;

		/**
		 * Creates connections with mongo database and unique indexes for every collections which
		 * store tokens.
		 * @param mongoConfig database configurations
		 * @throws Exception exception which might occur during establishing connection and creating indexes
		 */
	    public MongoManaged (MongoConfiguration mongoConfig) throws Exception {
	    	
	    	this.mongoClient = new MongoClient(mongoConfig.host, mongoConfig.port);
	    	this.db = this.mongoClient.getDatabase(mongoConfig.db);
	    	//so far there is no authenticate
	    	
	    	//create indexes for each collection
	    	this.db.getCollection("twitter").createIndex(new Document("token", 1), new IndexOptions().unique(true));
	    	this.db.getCollection("instagram").createIndex(new Document("token", 1), new IndexOptions().unique(true));
	    	this.db.getCollection("facebook").createIndex(new Document("token", 1), new IndexOptions().unique(true));
	    	this.db.getCollection("facebookPages").createIndex(new Document("pageAccessToken",1), new IndexOptions().unique(true));
	    }


	    @Override
	    public void start() throws Exception {

	    }

	    @Override
	    public void stop() throws Exception {
	        this.mongoClient.close();
	    }

	    /**
	     * Mongo client getter
	     * @return mongo client instance
	     */
	    public MongoClient getMongo() {
	        return this.mongoClient;
	    }

	    /**
	     * Database getter
	     * @return database instance
	     */
	    public MongoDatabase getDb() {
	        return this.db;
	    }
	}

