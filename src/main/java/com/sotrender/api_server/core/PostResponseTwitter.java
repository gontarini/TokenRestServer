package com.sotrender.api_server.core;

import java.io.IOException;

import org.bson.Document;

import com.sotrender.api_server.MyApplicationConfiguration.TwitterOauthConfiguration;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.*;

/**
 * Class extends PostResponse class with range of twitter token things.
 * @author pawel
 */
public class PostResponseTwitter extends PostResponse {

	/**
	 * Twitter secret token
	 */
	private String secretToken;

	/**
	 * Instance object of accessToken class from twitter4j lib
	 * 
	 */
	private AccessToken accessToken;

	/**
	 * Twitter object which is the same for every instance of the PostResponseTwitter class
	 */
	private static Twitter twitter;

	/**
	 * Constructor which invokes parent constructor.
	 * @param doc document retrieved from mongo database
	 */
	public PostResponseTwitter(Document doc) {
		super(doc);
	}

	/**
	 * Overriding method which create response object basing on given mongo document 
	 * and invoke checkToken method
	 */
	@Override
	public void createEntity() throws TwitterException {
		this.mongoId = this.doc.get("_id").toString();
		this.appId = this.doc.get("app_id").toString();

		this.token = this.doc.get("token").toString();
//		this.token = "1467720150-TsntRLk5Ig5yTLRtB3VBmIcYYQmk9GDz8tF9nxm";

		this.secretToken = this.doc.getString("secret").toString();
//		this.secretToken = "dF1wh5bYLzCc0QVfDLaOz1jjz9RcvFPNLoWPXYBy8agYz";

		checkToken();
	}

	/**
	 * Overriding method to make get request to twtiter api
	 * and retrieve certain data about specified user in such access token
	 */
	@Override
	protected void checkToken() throws TwitterException {
		this.accessToken = new AccessToken(this.token, this.secretToken);
		PostResponseTwitter.getTwitter().setOAuthAccessToken(this.accessToken);

		User user = PostResponseTwitter.getTwitter().verifyCredentials();
		this.name = user.getName();
		this.userId = user.getId();
		this.picture = user.getOriginalProfileImageURL();
	}

	/**
	 * Static method to authorize application with the read configurations from configuration file
	 * @param twitterConf twitter app configurations
	 * @throws TwitterException exception during veryfing app
	 * @throws IOException internal exception
	 */
	public static void twitterAuthentication(TwitterOauthConfiguration twitterConf) throws TwitterException,
			IOException {
		PostResponseTwitter.setTwitter(TwitterFactory.getSingleton());
		PostResponseTwitter.getTwitter().setOAuthConsumer(
				twitterConf.consumerKey,
				twitterConf.consumerSecret);
	}

	/**
	 * Getter method for twitter object
	 * @return twitter instance
	 */
	public static Twitter getTwitter() {
		return twitter;
	}

	/**
	 * Setter method for twitter object
	 * @param twitter 
	 */
	public static void setTwitter(Twitter twitter) {
		PostResponseTwitter.twitter = twitter;
	}

}
