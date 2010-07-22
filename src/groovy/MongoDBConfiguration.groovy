import org.codehaus.groovy.grails.commons.ConfigurationHolder;

import com.mongodb.Mongo;
import com.mongodb.ServerAddress;

/**
 * Holds configurations related to MongoDB.
 * 
 * @author jenglert
 */
class MongoDBConfiguration {
        
    /**
     * Static copy of the MongoDB connection.
     */
    static Mongo mongo;
    
    /**
     * The instance variable for the singleton
     */
    private static final instance = new MongoDBConfiguration();
    
    /**
     * Retrieves an instance of the singleton.
     */
    public static MongoDBConfiguration getInstance() {
        return instance;
    }
    
    /**
     * @return Mongo an instance of the MongoDB connector.
     */
    public static Mongo getMongo() {
        return mongo;
    }
    
    /**
     * Initializes the MongoDB configuration 
     */
    private MongoDBConfiguration() {
        initialize();
    }
    
    /**
     * @return String the name of the mongoDB database we will be using.
     */
    public String getDatabaseName() {
        return ConfigurationHolder.config.mongodb.databaseName;
    }
    
    /**
     * Initializes the MongoDB connection. 
     */
    private initialize() {
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
    }
       
    
    
}
