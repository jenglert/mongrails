import java.text.SimpleDateFormat;
import java.util.Date;

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
	 * Inserts a row into MongoDB.  The syntax for this type of request is:
	 * 
	 * http://<web server>/mongrails/rest/insert/<collection name>
	 * 
	 * You should include a json object as the body of the post request.
	 */
	def insert = {
		
        Mongo mongo = MongoDBConfiguration.getMongo();
		
		DB db =  mongo.getDB(MongoDBConfiguration.getInstance().getDatabaseName());
		
		DBCollection coll = db.getCollection(params."id")
		
		String requestBody = IOUtils.toString(request.getInputStream());
		BasicDBObject doc = JsonToBsonConverter.convertJsonToBson(requestBody);
		
		coll.insert(doc);
		
		render "processed"
	}
    
    /**
     * Scans the request parameters and persists all the request parameters to the specified collection.
     */
    def log = {
        Mongo mongo = MongoDBConfiguration.getMongo();
        
        DB db =  mongo.getDB(MongoDBConfiguration.getInstance().getDatabaseName());
        
        DBCollection coll = db.getCollection(params."id")
        
        BasicDBObject doc = new BasicDBObject();
        
        for (String key : params.keySet()) {
            doc.put(key, params[key]);
        }
        
        BasicDBObject baseInformation = new BasicDBObject();
        
        baseInformation.put("date", new SimpleDateFormat("yyyyMMdd").format(new Date()));
        baseInformation.put("time", new SimpleDateFormat("HHmmss").format(new Date()));
        baseInformation.put("referrer", request.getHeader("referer"));
        
        doc.put("baseInformation", baseInformation);
        
        coll.insert(doc);
        
        render "";
        
    }
}
