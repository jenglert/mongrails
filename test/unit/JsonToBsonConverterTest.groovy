import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

class JsonToBsonConverterTest extends GroovyTestCase {

	void testBasicConversion() {
		String jsonString = "{'x': 1, 'y': 2}"
		BasicDBObject dbObject = JsonToBsonConverter.convertJsonToBson(jsonString)
		
		assertNotNull dbObject;
		assertEquals 2, dbObject.size()
		assertEquals "1", dbObject.getAt("x")
		assertEquals "2", dbObject.getAt("y")
	}
	
	void testEmptyJsonString() {
		String jsonString = "{}"
		
		BasicDBObject dbObject = JsonToBsonConverter.convertJsonToBson(jsonString)
		
		assertNotNull dbObject;
		assertEquals 0, dbObject.size()
	}
	
	void testSubJsonString() {
		String jsonString = "{'x': 1, 'y': {'a' : 3, 'b': {'deer': 'jumpy'}}}"
		BasicDBObject dbObject = JsonToBsonConverter.convertJsonToBson(jsonString)
		
		assertNotNull dbObject;
		assertEquals 2, dbObject.size()
		assertEquals "1", dbObject.getAt("x")
		assertTrue dbObject.getAt("y") instanceof DBObject
		assertEquals "3", dbObject.getAt("y").getAt("a")
		assertEquals "jumpy", dbObject.getAt("y").getAt("b").getAt("deer")
	}
	
	void testListOfJson() {
		String jsonString = "{'x' : ['one', 'two', 'three']}"
		BasicDBObject dbObject = JsonToBsonConverter.convertJsonToBson(jsonString)
		
		assertNotNull dbObject;
		assertEquals 1, dbObject.size()
		assertTrue dbObject.getAt("x") instanceof BasicDBList
		BasicDBList dbList = dbObject.getAt("x")
		assertEquals "one", dbList.get(0)
		assertEquals "two", dbList.get(1)
		assertEquals "three", dbList.get(2)
		
	}
}