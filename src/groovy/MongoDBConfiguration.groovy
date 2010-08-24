import org.apache.log4j.Logger;
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
     * Logger for this class
     */
    private static final Logger log = Logger.getLogger(MongoDBConfiguration.class);
    
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
        return ConfigurationHolder.flatConfig."mongodb.databaseName";
    }
    
    /**
     * Initializes the MongoDB connection. 
     */
    private initialize() {
        // Initialize mongo if it hasn't be initialized previously.
        if (mongo == null) {
            ServerAddress primary = new ServerAddress(ConfigurationHolder.flatConfig."mongodb.primary.server.address",
                Integer.valueOf(ConfigurationHolder.flatConfig."mongodb.primary.server.port"));
            
            List<ServerAddress> servers = new ArrayList<ServerAddress>();
            servers.add(primary);
            
            // configure a secondary address if present.
            ServerAddress secondary = null;
            if (ConfigurationHolder.flatConfig."mongodb.secondary.server.address" instanceof String) {
                secondary = new ServerAddress(ConfigurationHolder.flatConfig."mongodb.secondary.server.address",
                    Integer.valueOf(ConfigurationHolder.flatConfig."mongodb.secondary.server.port"));
                
                servers.add(secondary);
            }
            
            // configure a secondary address if present.
            ServerAddress third = null;
            if (ConfigurationHolder.flatConfig."mongodb.third.server.address" instanceof String) {
                third = new ServerAddress(ConfigurationHolder.flatConfig."mongodb.third.server.address",
                    Integer.valueOf(ConfigurationHolder.flatConfig."mongodb.third.server.port"));
                
                servers.add(third);
            }
            
            // configure a secondary address if present.
            ServerAddress fourth = null;
            if (ConfigurationHolder.flatConfig."mongodb.fourth.server.address" instanceof String) {
                fourth = new ServerAddress(ConfigurationHolder.flatConfig."mongodb.fourth.server.address",
                    Integer.valueOf(ConfigurationHolder.flatConfig."mongodb.fourth.server.port"));
                
                servers.add(fourth);
            }
            
            // configure a secondary address if present.
            ServerAddress fifth = null;
            if (ConfigurationHolder.flatConfig."mongodb.fifth.server.address" instanceof String) {
                fifth = new ServerAddress(ConfigurationHolder.flatConfig."mongodb.fifth.server.address",
                    Integer.valueOf(ConfigurationHolder.flatConfig."mongodb.fifth.server.port"));
                
                servers.add(fifth);
            }
            
            for (ServerAddress server: servers) {
                System.out.println("Loaded server: " + server.getHost() + ":" + server.getPort());
            }
            
            mongo = new Mongo(servers);
        }
    }
       
    
    
}
