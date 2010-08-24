import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

class RestControllerTest extends GroovyTestCase {

	public voidTestInsertData() {
		PostMethod method = new PostMethod("http://184.73.247.21:8080/mongrails/rest/insert/test");
		method.setRequestBody("{'x': 1, 'y': 2}");
		
		HttpClient client = new HttpClient();
		client.executeMethod(method);
		
		assertEquals 200, method.getStatusCode()
		assertNotNull method.getResponseBodyAsString()
		assertEquals "processed", method.getResponseBodyAsString()
	}
}