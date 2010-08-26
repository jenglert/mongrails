import org.codehaus.groovy.runtime.typehandling.GroovyCastException;


import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;


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
    
    /**
     * Retrieves the count of objects in the database.
     */
    def count = {
        Mongo mongo = MongoDBConfiguration.getMongo();
        
        DB db =  mongo.getDB(MongoDBConfiguration.getInstance().getDatabaseName());
     
        DBCollection coll = db.getCollection(params."id");
        
        Map<String, QueryClause> queryClauses = createQueryClause(request);
        
        DBCursor cursor;
        if (queryClauses.size() == 0) {
            cursor = coll.find();
        }
        else {
            BasicDBObject query = new BasicDBObject();
            for (QueryClause clause : queryClauses.values()) {
                query.put(clause.getParameter(), new BasicDBObject(clause.getOperator(), clause.getOperand()));
            }
            cursor = coll.find(query);
        }
        
        render cursor.count()
    }
    
    /**
     * Retrieves objects in an XML format
     */
    def retrieve = {
        Mongo mongo = MongoDBConfiguration.getMongo();
        
        DB db =  mongo.getDB(MongoDBConfiguration.getInstance().getDatabaseName());
     
        DBCollection coll = db.getCollection(params."id");
        
        String colsToRetrieve = request.getParameter("cols");
        
        String[] cols = StringUtils.split(colsToRetrieve, ',');
        
        Map<String, QueryClause> queryClauses = createQueryClause(request);
        
        DBCursor cursor;
        if (queryClauses.size() == 0) {
            cursor = coll.find();
        }
        else {
            BasicDBObject query = new BasicDBObject();
            for (QueryClause clause : queryClauses.values()) {
                query.put(clause.getParameter(), new BasicDBObject(clause.getOperator(), clause.getOperand()));
            }
            cursor = coll.find(query);
        }
        
        StringBuilder results = new StringBuilder();
        results.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        results.append("<" + params."id" + ">\n")
        while (cursor.hasNext()) {
            DBObject dbObject = cursor.next()
            results.append("<item>\n")
            
            for (String col : cols) {
                Object result = dbObject;
                
                String[] levels = StringUtils.split(col, '.');
                for (String level : levels) {
                    try {
                        result = ((DBObject) result).get(level);
                    }
                    catch (GroovyCastException gce) {
                        render "Unable to find value in object: " + col;
                        return
                    }
                }
                
                
                results.append("<" + col.replace('.', '-') + ">\n")
                results.append(result);
                results.append("</" + col.replace('.', '-') + ">\n")
            }
            
            results.append("</item>\n")
        }
        results.append("</" + params."id" + ">\n")
        
        response.setContentType("text/xml");
        
        render results;
    }
    
    /**
     * Creates a map of query clauses based on parameters from the request.
     */
    private Map<String, QueryClause> createQueryClause(HttpServletRequest request) {
        Map<String, QueryClause> queryClauses = new HashMap<String, QueryClause>();
        
        
        // Iterate over all the objects and find any query clauses.
        for (String param : request.getParameterMap().keySet()) {
            
            if (param.endsWith("PARAM")) {
                String name = param.replace("PARAM", "");
                
                
                QueryClause clause = queryClauses.get(name);
                if (clause == null) {
                    clause = new QueryClause();
                }
                
                clause.setParameter(request.getParameter(param));
                
                queryClauses.put(name, clause);
            }
            
            if (param.endsWith("OPERATOR")) {
                String name = param.replace("OPERATOR", "");
                
                
                QueryClause clause = queryClauses.get(name);
                if (clause == null) {
                    clause = new QueryClause();
                }
                
                clause.setOperator(request.getParameter(param));
                
                queryClauses.put(name, clause);
            }
            
            if (param.endsWith("OPERAND")) {
                String name = param.replace("OPERAND", "");
                
                
                QueryClause clause = queryClauses.get(name);
                if (clause == null) {
                    clause = new QueryClause();
                }
                
                clause.setOperand(request.getParameter(param));
                
                queryClauses.put(name, clause);
            }
        }
        
        for (String name : new HashMap<String, QueryClause>(queryClauses).keySet()) {
            if (!queryClauses.get(name).isValid()) {
                queryClauses.remove(name);
                System.out.println("Unable to create query clause for " + name);
            }
        }
        
        return queryClauses;
    }
}
