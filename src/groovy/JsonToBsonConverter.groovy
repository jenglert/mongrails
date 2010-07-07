import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

/**
 * Utility class used for converting Json String into BasicDBObjects that can be imported in MongoDB.
 * 
 * @author jenglert
 */
class JsonToBsonConverter {

	/**
	* Concerts a JSON string into a BSON object that can be persisted into MongoDB
	*
	* @param jsonString The json String
	* @return BasicDBObject a basic BSON object that has been constructed.
	*/
   public static BasicDBObject convertJsonToBson(String jsonString) {
	   JsonElement element = new JsonParser().parse(jsonString);
	   
	   return (BasicDBObject) convertJsonElementToBson(element);
   }
   
   /**
	* Recursive function that converts Json element to parse into BSON elements.
	*
	* @param element the JsonElement to convert
	* @return Object either a string or BasicDBObject, depending on the situation.
	*/
   private static Object convertJsonElementToBson(JsonElement element) {
	   if (element.isJsonObject()) {
		   JsonObject jsonObject = (JsonObject) element;
		   BasicDBObject baseObject = new BasicDBObject();
		   
		   for (Map.Entry<String,JsonElement> elementEntry : jsonObject.entrySet()){
			   Object recursionResult = convertJsonElementToBson(elementEntry.getValue());
			   baseObject.put(elementEntry.getKey(), recursionResult);
		   }
		   
		   return baseObject;
	   }
	   else if (element.isJsonArray()){
		   JsonArray array = (JsonArray) element;
		   BasicDBList bsonList = new BasicDBList()
		   for (int i = 0; i < array.size(); i++) {
			   bsonList.put(i, array.get(i).getAsString());
		   }
		   
		   return bsonList;
	   }
	   else if (element.isJsonPrimitive()) {
		   return element.getAsString();
	   }
	   else if (element.isJsonNull()) {
		   return "";
	   }
	   
	   throw new RuntimeException("Unable to convert JsonElement of type " + element.getClass().toString() + " to a BasicDBObject");
   }
}
