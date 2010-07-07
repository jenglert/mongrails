import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import com.mongodb.*;

/**
 * Primary entry point for the MongDB rest controller.
 * 
 * @author jenglert
 */
class RestController  {
	
	/**
	 * Static copy of the MongoDB connection.
	 */
	static Mongo mongo;

	/**	
	 * Inserts a row into MongoDB.
	 */
	def insert = {
		// Initialize mongo if it hasn't be initialized previously.	
		if (mongo == null) {
			ServerAddress primary = new ServerAddress(ConfigurationHolder.flatConfig."mongodb.primary.server.address", 
				ConfigurationHolder.flatConfig."mongodb.primary.server.port");
			
			// configure a secondary address if present.
			ServerAddress secondary = null;
			if (ConfigurationHolder.flatConfig."mongodb.secondary.server.address" instanceof String) {
				secondary = new ServerAddress(ConfigurationHolder.flatConfig."mongodb.secondary.server.address", 
					Integer.valueOf(ConfigurationHolder.flatConfig."mongodb.secondary.server.port"));
			}
			
			if (secondary != null) {
				mongo = new Mongo(primary, secondary);
			}
			else {
				mongo = new Mongo(primary);
			}
		}
		
		DB db =  mongo.getDB(ConfigurationHolder.config.mongodb.databaseName);
		
		DBCollection coll = db.getCollection(params."id")
		
		String requestBody = IOUtils.toString(request.getInputStream());
		BasicDBObject doc = JsonToBsonConverter.convertJsonToBson(requestBody);
		
		coll.insert(doc);
		
		render "processed"
	}
}
