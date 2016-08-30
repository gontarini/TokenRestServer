package com.sotrender.api_server.resources;

import java.io.IOException;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpException;
import org.apache.http.protocol.ResponseContent;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;

import twitter4j.TwitterException;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.DeleteResult;
import com.sotrender.api_server.Configurations.FacebookConfiguration;
import com.sotrender.api_server.Configurations.InstagramConfigurations;
import com.sotrender.api_server.Configurations.Schema;
import com.sotrender.api_server.Configurations.TwitterOauthConfiguration;
import com.sotrender.api_server.core.GetResponse;
import com.sotrender.api_server.core.GetResponseFacebook;
import com.sotrender.api_server.core.GetResponsePageFacebook;
import com.sotrender.api_server.core.GetResponseTwitter;
import com.sotrender.api_server.core.PostResponse;
import com.sotrender.api_server.core.PostResponseFacebook;
import com.sotrender.api_server.core.PostResponseInstagram;
import com.sotrender.api_server.core.PostResponseTwitter;
import com.sotrender.api_server.db.MongoManaged;
import com.sotrender.api_server.entities.AccessLevelWage;
import com.sotrender.api_server.exceptions.EntityInMongo;
//import com.sotrender.api_server.exceptions.EntityNotFoundMapper;
import com.sotrender.api_server.exceptions.JsonExceptionMapper;
import com.sotrender.api_server.exceptions.JsonValidation;
import com.sotrender.api_server.exceptions.TokenExpired;
import com.sotrender.api_server.exceptions.ValidationFailure;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main class implements resources in the following project
 * 
 * @author pawel
 * 
 */
@SuppressWarnings("unused")
@Path("/channel")
@Produces(MediaType.APPLICATION_JSON)
public class Resources {

	/**
	 * entry point to make connection with db and set their configurations
	 */
	private MongoManaged mongoManaged;

	/**
	 * instance of mongoCollection object. Gives possibility to have collection
	 * locally
	 */
	private MongoCollection collection;

	/**
	 * class implements response object for Twitter and throw specific exception
	 * during token check
	 */
	private PostResponseTwitter postResponseTwitter;

	/**
	 * class implements response object for Facebook and throw specific
	 * exception during token check
	 */
	private PostResponseFacebook postResponseFacebook;

	/**
	 * class implements response object for Instagram and throw specific
	 * exception during token check
	 */
	private PostResponseInstagram postResponseInstagram;

	/**
	 * Unique token which indicates document do be removed, because of
	 * invalidness
	 */
	private String currentToken;

	/**
	 * Instance of getResponse object
	 */
	private GetResponse getResponse;

	/**
	 * Instance of getResponseTwitter
	 */
	private GetResponseTwitter getResponseTwitter;
	/**
	 * Instance of getResponsePagefacebook class which create response for
	 * request to page access token
	 */
	private GetResponsePageFacebook getResponsePageFacebook;

	/**
	 * List of facebook application configuration
	 */
	private List<FacebookConfiguration> facebookConfigurations;

	/**
	 * List of twitter application configuration
	 */
	private List<TwitterOauthConfiguration> twitterConfigurations;

	/**
	 * Schema file of twitter json
	 */
	private String schemaTwitter;

	/**
	 * Facebook app id essential to set up authorizathion
	 */
	private String appId;

	/**
	 * Particular facebook application secret key
	 */
	private String appSecret;

	/**
	 * Twitter consumer secret key
	 */
	private String consumerSecret;

	/**
	 * Twitter consumer key
	 */
	private String consumerKey;

	/**
	 * Schema file of facebook and instagram json
	 */
	private String schemaFaceInsta;

	/**
	 * Boolean parameter which indicates whether given appId or appSecret in
	 * json file is correct
	 */
	private boolean isvalid;

	/**
	 * List of instagram application configurations
	 */
	private List<InstagramConfigurations> instagramConfigurations;

	/**
	 * Mongo identifier
	 */
	private ObjectId id;

	/**
	 * Facebook instance of object to response
	 */
	private GetResponseFacebook getResponseFacebook;

	/**
	 * Constructor sets up all configurations and set mongoManaged instance
	 * field. Whatsmore, it loads json schema files and convert them to string
	 * type
	 * 
	 * @param mongoManaged
	 *            mongoManage instance
	 * @param facebookConf
	 *            facebook configurations
	 * @param twitterConf
	 *            twitter configurations
	 * @param instagramConf
	 *            instagram configurations
	 * @param schema
	 *            schema paths
	 * @throws TwitterException
	 *             twitter exception
	 * @throws IOException
	 *             internal exception
	 */
	public Resources(MongoManaged mongoManaged,
			List<FacebookConfiguration> facebookConf,
			List<TwitterOauthConfiguration> twitterConf,
			List<InstagramConfigurations> instagramConf, Schema schema)
			throws TwitterException, IOException {

		this.isvalid = false;

		this.mongoManaged = mongoManaged;
		final org.slf4j.Logger logger = LoggerFactory
				.getLogger(Resources.class);

		this.facebookConfigurations = facebookConf;
		this.twitterConfigurations = twitterConf;
		this.instagramConfigurations = instagramConf;

		File schemaFileTwitter = new File(schema.twitter);
		File schemaFileFaceInsta = new File(schema.faceInsta);

		schemaFaceInsta = new Scanner(schemaFileFaceInsta).useDelimiter("\\Z")
				.next();
		schemaTwitter = new Scanner(schemaFileTwitter).useDelimiter("\\Z")
				.next();

		// PostResponseTwitter.twitterAuthentication(twitterConf.get(0));
		// PostResponseFacebook.setUpConfiguration(facebookConf.get(0));
	}

	/**
	 * Http post method which consume and produce json object. Client should
	 * send json file which have strict fields and looks like the one below:
	 * 
	 * Facebook: {"app_id":{appId}, "token" : {token}, "source" : {sourcePlace}
	 * }
	 * 
	 * 
	 * Twitter: {"app_id":{appID}, "token" : {accessToken}, "secret" :
	 * {ApplicationSecretToken}, "source" : {sourcePlace} }
	 * 
	 * Instagram: {"app_id":{appId} , "token" :{token}, "source" : {sourcePlace}
	 * }
	 * 
	 * To be more familiar with json file fields, check out json schema files
	 * and check types of particular fields
	 * 
	 * @param channelName
	 *            social media channel name
	 * @param message
	 *            json to be parsed
	 * @return Response object to client side
	 * @throws IOException
	 *             internal exception
	 */
	@POST
	@Timed
	@Path("/{channelName}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@PathParam("channelName") String channelName,
			@Valid String message) throws IOException {

		final org.slf4j.Logger logger = LoggerFactory
				.getLogger(Resources.class);
		logger.info("Resources Insert Request Recieved" + "\n" + message);

		this.isvalid = false;
		try {
			this.checkValidation(message, channelName);
			if (this.isvalid == false) {
				return new ValidationFailure()
						.toResponse(new ProcessingException(
								"There is no app which can handle your token. Please, check given app data"));
			}

			this.collection = this.mongoManaged.getDb().getCollection(
					channelName);

			try {
				Document doc = this.documentGet(message);
				doc.append("usageCount", 1);

				this.collection.insertOne(doc);
				this.id = doc.getObjectId("_id");

				FindIterable iterable = this.collection.find(new Document(
						"_id", this.id));

				Document saved = (Document) iterable.first();

				return this.createResponse(channelName, saved);
			} catch (JsonParseException e) {
				System.out.println("Check out correctness of given json..");
				return new JsonExceptionMapper().toResponse(e);
			} catch (MongoException ex) {

				Document doc = documentGet(message);
				doc.append("usageCount", 1);
				this.collection.deleteOne(new Document("token",
						this.currentToken));
				this.collection.insertOne(doc);

				try {
					this.createResponse(channelName, doc);
				} catch (TwitterException e) {
					return this.catchTwitterException(e, message);
				} catch (TokenExpired e) {
					return this.catchTokenExpired(e, channelName, message);
				}

				return new EntityInMongo().toResponse(new MongoException(
						"Updated token"));

			} catch (TokenExpired e) {
				// throw exception of token incorrectness
				return this.catchTokenExpired(e, channelName, message);

			} catch (TwitterException e) {
				// rate limit exceeded
				return this.catchTwitterException(e, message);
			}

		} catch (ProcessingException e1) {
			e1.printStackTrace();
			return new ValidationFailure().toResponse(e1);
		}
	}

	/**
	 * Method which checks whether given json fits to loaded schema and then
	 * compare certain field in it to the ones read from configuration file
	 * (appId). If the field fits, loop over configuration objects is broken and
	 * validation is correct, otherwise it goes to another configuration object.
	 * If it is not found an object suitable for the given json, validation
	 * report is failure
	 * 
	 * @param message
	 * @param channelName
	 * @throws ProcessingException
	 * @throws IOException
	 */
	private void checkValidation(String message, String channelName)
			throws ProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(message);

		if (channelName.equals("facebook")) {
			if (JsonValidation.isJsonValid(schemaFaceInsta, message)) {
				System.out.println("Valid!");
				JsonNode appId = rootNode.path("app_id");

				for (FacebookConfiguration config : this.facebookConfigurations) {
					System.out.println(config.getAppId());
					if (config.getAppId().equals(appId.asText())) {
						System.out.println("Completeley valid facebook");
						this.appId = appId.asText();
						this.appSecret = config.getSecret();
						this.isvalid = true;
						break;
					}
				}
			} else {
				throw new ProcessingException(
						"Json is not fit to facebook json schema");

			}
		} else if (channelName.equals("instagram")) {
			if (JsonValidation.isJsonValid(schemaFaceInsta, message)) {
				System.out.println("Valid!");
				JsonNode appId = rootNode.path("app_id");
				for (InstagramConfigurations config : this.instagramConfigurations) {
					if (config.getAppId().equals(appId.asText())) {
						System.out.println("Completeley valid instagram");

						this.appId = appId.asText();
						this.isvalid = true;
						break;
					}
				}
			} else {
				throw new ProcessingException(
						"Json is not fit to instagram json schema");
			}
		} else {
			if (JsonValidation.isJsonValid(schemaTwitter, message)) {
				System.out.println("Valid!");
				JsonNode appId = rootNode.path("app_id");
				JsonNode appSecret = rootNode.path("secret");

				for (TwitterOauthConfiguration twConfig : this.twitterConfigurations) {
					System.out.println(twConfig.getAppId());
					System.out.println(twConfig.getAppId().equals(
							appId.asText()));
					if (twConfig.getAppId().equals(appId.asText())) {
						System.out.println("Completeley valid twitter");

						this.consumerSecret = twConfig.getConsumerSecret();
						this.consumerKey = twConfig.getConsumerKey();

						this.isvalid = true;
						break;
					}
				}
			} else {
				throw new ProcessingException(
						"Json is not fit to twitter json schema");
			}
		}
	}

	/**
	 * Method which choose a way to behave based on channel name given as a
	 * parameter. Basically, it creates response object, set up configurations
	 * if necessary, create fields in that object and return accepting response.
	 * 
	 * @param channelName
	 *            facebook, twitter or instagram
	 * @param doc
	 *            document from mongoDB
	 * @return response object to be sent to client
	 * @throws TokenExpired
	 * @throws TwitterException
	 * @throws IOException
	 */
	private Response createResponse(String channelName, Document doc)
			throws TokenExpired, TwitterException, IOException {
		if (channelName.equals("facebook")) {
			postResponseFacebook = new PostResponseFacebook(doc);
			postResponseFacebook.setUpConfiguration(this.appId, this.appSecret);
			postResponseFacebook.createEntity();
			postResponseFacebook.addPageTokensToMongo(this.mongoManaged);

			this.collection.updateOne(new Document("_id", this.id),
					new Document("$set", new Document("permissions",
							postResponseFacebook.permissions)));

			return Response.accepted(postResponseFacebook).build();

		} else if (channelName.equals("twitter")) {
			postResponseTwitter = new PostResponseTwitter(doc);
			postResponseTwitter.twitterAuthentication(this.consumerKey,
					this.consumerSecret);
			postResponseTwitter.createEntity();
			return Response.accepted(postResponseTwitter).build();

		} else if (channelName.equals("instagram")) {
			postResponseInstagram = new PostResponseInstagram(doc);
			postResponseInstagram.createEntity();
			return Response.accepted(postResponseInstagram).build();
		}
		return null;
	}

	/**
	 * Method which handles exceptions over twitter
	 * 
	 * @param ex
	 * @param message
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	private Response catchTwitterException(TwitterException ex, String message)
			throws JsonProcessingException, IOException {
		MongoCollection<Document> collectionInvalid = this.mongoManaged.getDb()
				.getCollection("twitterInvalid");

		Document doc = documentGet(message);
		collectionInvalid.insertOne(doc);

		this.collection.deleteOne(new Document("token", this.currentToken));

		return new TokenExpired().toResponse(ex);
	}

	private Response catchTokenExpired(TokenExpired ex, String channelName,
			String message) throws JsonProcessingException, IOException {
		String collectionInvalidName = channelName + "Invalid";

		MongoCollection<Document> collectionInvalid = this.mongoManaged.getDb()
				.getCollection(collectionInvalidName);

		Document doc = documentGet(message);
		collectionInvalid.insertOne(doc);

		this.collection.deleteOne(new Document("token", this.currentToken));

		return new TokenExpired()
				.toResponse(new HttpException("Token expired"));
	}

	/**
	 * Method to prepare document to put into mongoDb
	 * 
	 * @param message
	 *            message to be converted into Document
	 * @return prepared document
	 * @throws JsonProcessingException
	 *             expection connected with parsing given message
	 * @throws IOException
	 *             internal exception connected with mapping message
	 */
	private Document documentGet(String message)
			throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(message);
		JsonNode tokenNode = rootNode.path("token");

		this.currentToken = tokenNode.asText();

		Document doc = mapper.readValue(message, Document.class);
		return doc;
	}

	/**
	 * Http het request method. Return object consist with elements like: token,
	 * secret (twitter), appId, date of add
	 * 
	 * @param channelName
	 * @return
	 */
	@GET()
	@Timed
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{channelName}")
	public Response get(@PathParam("channelName") String channelName) {

		final org.slf4j.Logger logger = LoggerFactory
				.getLogger(Resources.class);
		logger.info("Resources GET Request Recieved");

		this.collection = this.mongoManaged.getDb().getCollection(channelName);

		FindIterable iterable = this.collection.find()
				.sort(new Document("usageCount", 1)).limit(1);
		Document document = (Document) iterable.first();
		// System.out.println(document);

		if (document.isEmpty()) {
			return Response.status(404).build();
		} else {

			int count = (Integer) document.get("usageCount");
			count++;

			try {
				String token = document.get("token").toString();

				this.collection
						.updateOne(new Document("token", token), new Document(
								"$set", new Document("usageCount", count)));
			} catch (org.bson.BSONException ex) {
				ex.getMessage();
			}
			if (channelName.equals("twitter")) {
				getResponseTwitter = new GetResponseTwitter(document);
				return Response.accepted(getResponseTwitter).build();
			} else if (channelName.equals("facebook")) {
				getResponseFacebook = new GetResponseFacebook(document, false);
				return Response.accepted(getResponseFacebook).build();
			} else {
				getResponse = new GetResponse(document, false);
				return Response.accepted(getResponse).build();
			}

		}
	}

	/**
	 * Http delete request method. It deletes an specified object in given
	 * parameters
	 * 
	 * @param channelName
	 * @param id
	 * @return
	 */
	@DELETE
	@Timed
	@Path("/{channelName}/{id}")
	public Response delete(@PathParam("channelName") String channelName,
			@PathParam("id") String id) {

		final org.slf4j.Logger logger = LoggerFactory
				.getLogger(Resources.class);
		logger.info("Resources DELETE Request Recieved " + id);

		this.collection = this.mongoManaged.getDb().getCollection(channelName);

		DeleteResult result = this.collection.deleteOne(new Document("_id",
				new ObjectId(id)));
		System.out.println(result);
		if (result.getDeletedCount() != 0) {
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}
	}

	/**
	 * Http get request method. It returns random object among objects stored in
	 * mongodb with certain appId
	 * 
	 * @param channelName
	 * @param appId
	 * @return
	 */
	@GET()
	@Timed
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{channelName}/{appId}")
	public Response getTokenByAppId(
			@PathParam("channelName") String channelName,
			@PathParam("appId") String appId) {
		// check if any element is already inside if not handle it and throw
		// exception

		final org.slf4j.Logger logger = LoggerFactory
				.getLogger(Resources.class);
		logger.info("Resources GET app token Request Recieved");

		this.collection = this.mongoManaged.getDb().getCollection(channelName);

		FindIterable iterable = this.collection
				.find(new Document("app_id", appId))
				.sort(new Document("usageCount", 1)).limit(1);
		Document document = (Document) iterable.first();
		System.out.println(document);

		if (document.isEmpty()) {
			return Response.status(404).build();
		} else {

			int count = (Integer) document.get("usageCount");
			count++;

			try {
				String token = document.get("token").toString();

				this.collection
						.updateOne(new Document("token", token), new Document(
								"$set", new Document("usageCount", count)));
			} catch (org.bson.BSONException ex) {
				ex.getMessage();
			}
			if (channelName.equals("twitter")) {
				getResponseTwitter = new GetResponseTwitter(document);
				return Response.accepted(getResponseTwitter).build();
			} else if (channelName.equals("facebook")) {
				getResponseFacebook = new GetResponseFacebook(document, false);
				return Response.accepted(getResponseFacebook).build();
			} else {
				getResponse = new GetResponse(document, false);
				return Response.accepted(getResponse).build();
			}

		}
	}

	/**
	 * Http get request method. It returns specified object by given parameter
	 * (id)
	 * 
	 * @param channelName
	 * @param id
	 * @return
	 */
	@GET()
	@Timed
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{channelName}/id/{id}")
	public Response getTokenById(@PathParam("channelName") String channelName,
			@PathParam("id") String id) {
		// check if any element is already inside if not handle it and throw
		// exception

		final org.slf4j.Logger logger = LoggerFactory
				.getLogger(Resources.class);
		logger.info("Resources GET app token Request Recieved");

		this.collection = this.mongoManaged.getDb().getCollection(channelName);

		FindIterable iterable = this.collection.find(new Document("_id",
				new ObjectId(id)));

		Document document = (Document) iterable.first();
		System.out.println(document);

		if (document.isEmpty()) {
			return Response.status(404).build();
		} else {

			int count = (Integer) document.get("usageCount");
			count++;

			try {
				String token = document.get("token").toString();

				this.collection
						.updateOne(new Document("token", token), new Document(
								"$set", new Document("usageCount", count)));
			} catch (org.bson.BSONException ex) {
				ex.getMessage();
			}
			if (channelName.equals("twitter")) {
				getResponseTwitter = new GetResponseTwitter(document);
				return Response.accepted(getResponseTwitter).build();
			} else if (channelName.equals("facebook")) {
				getResponseFacebook = new GetResponseFacebook(document, false);
				return Response.accepted(getResponseFacebook).build();
			} else {
				getResponse = new GetResponse(document, false);
				return Response.accepted(getResponse).build();
			}

		}
	}

	@GET()
	@Timed
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/facebook/page/{appId}/{pageId}")
	public Response getPageToken(@QueryParam("permissions") String permissions,
			@QueryParam("accessLevel") String accessLevel,
			@PathParam("appId") String appId, @PathParam("pageId") String pageId) {

		// check if any element is already inside if not handle it and throw
		// exception

		final org.slf4j.Logger logger = LoggerFactory
				.getLogger(Resources.class);
		logger.info("Resources GET page token Request Recieved, list size:  "
				+ permissions);

		this.collection = this.mongoManaged.getDb().getCollection(
				"facebookPages");

		BasicDBObject query = new BasicDBObject();

		AccessLevelWage wage = new AccessLevelWage();
		if (permissions != null) {
			String[] perms = permissions.split(",");
			List<String> list = new ArrayList<String>();
			for (String permission : perms) {
				list.add(permission);
			}
			query.put("permissions", new BasicDBObject("$in", list));
		}
		if (accessLevel != null) {
			query.put("accessLevelWage",
					new BasicDBObject("$lte", wage.get(accessLevel)));
		}

		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("app_id", appId));
		obj.add(new BasicDBObject("pageId", pageId));
		query.put("$or", obj);

		
		System.out.println(query);
		FindIterable iterable = this.collection.find(query).sort(new Document("usageCount", 1)).limit(1);;

		Document document = (Document) iterable.first();
		System.out.println(document);

		if (iterable.iterator().hasNext()) {
			int count = (Integer) document.get("usageCount");
			count++;

			try {
				String token = document.get("pageAccessToken").toString();

				this.collection.updateOne(
						new Document("pageAccessToken", token), new Document(
								"$set", new Document("usageCount", count)));
			} catch (org.bson.BSONException ex) {
				ex.getMessage();
			}
			getResponsePageFacebook = new GetResponsePageFacebook(document);
			return Response.accepted(getResponsePageFacebook).build();
		} else {
			return new EntityInMongo().toResponse("There is no token which perform all of constraints");
		}
	}

	@POST
	@Timed
	@Path("/{channelName}/report/{id}")
	public Response reportChannelToken(
			@PathParam("channelName") String channelName,
			@PathParam("id") String id) {

		final org.slf4j.Logger logger = LoggerFactory
				.getLogger(Resources.class);
		logger.info("Resources Report Request Recieved about token: " + "\n"
				+ id + " in " + channelName);
		this.collection = this.mongoManaged.getDb().getCollection(channelName);

		FindIterable iterable = this.collection.find(new Document("_id",
				new ObjectId(id)));

		Document document = (Document) iterable.first();
		document.remove("usageCount");
		DeleteResult result = this.collection.deleteOne(new Document("_id",
				new ObjectId(id)));

		String invalidTokens = channelName + "Invalid";
		this.collection = this.mongoManaged.getDb()
				.getCollection(invalidTokens);
		this.collection.insertOne(document);

		if (result.getDeletedCount() != 0) {
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}
	}

	@POST
	@Timed
	@Path("/facebook/page/report/{id}")
	public Response reportPageToken(@PathParam("id") String id) {

		final org.slf4j.Logger logger = LoggerFactory
				.getLogger(Resources.class);
		logger.info("Resources Report Request Recieved about page token: "
				+ "\n" + id);
		String channelName = "facebookPages";
		this.collection = this.mongoManaged.getDb().getCollection(channelName);

		FindIterable iterable = this.collection.find(new Document("_id",
				new ObjectId(id)));

		Document document = (Document) iterable.first();
		document.remove("usageCount");
		DeleteResult result = this.collection.deleteOne(new Document("_id",
				new ObjectId(id)));

		String invalidTokens = channelName + "Invalid";
		this.collection = this.mongoManaged.getDb()
				.getCollection(invalidTokens);
		this.collection.insertOne(document);

		if (result.getDeletedCount() != 0) {
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}
	}

	@DELETE
	@Timed
	@Path("/facebook/page/{id}")
	public Response delete(@PathParam("id") String id) {

		final org.slf4j.Logger logger = LoggerFactory
				.getLogger(Resources.class);
		logger.info("Resources DELETE page token Request Recieved " + id);

		this.collection = this.mongoManaged.getDb().getCollection(
				"facebookPages");

		DeleteResult result = this.collection.deleteOne(new Document("_id",
				new ObjectId(id)));
		System.out.println(result);
		if (result.getDeletedCount() != 0) {
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}
	}

}
