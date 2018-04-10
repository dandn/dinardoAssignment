package assignmentQA;

import static org.junit.Assert.*;

import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

public class US05_CheckRESTAPITest {

	@Before
	public void setUp() throws Exception {
		InitializeEnvironment.initializeUsingREST(20);
	}

	@Test
	public void testGet_ValidId() {
		RestAssured.baseURI = InitializeEnvironment.getWebAppLocation();
		RequestSpecification httpRequest = RestAssured.given();
		Response response = RestHelper.restGet(httpRequest, "/api/pets/10");
		
		Headers headers = response.getHeaders();
		String contentType = headers.getValue("Content-Type");
		String[] contentTypeParts = contentType.split(";");
		assertEquals("application/json", contentTypeParts[0]);
		
		// get status code
		int statusCode = response.getStatusCode();
		assertEquals("Check status code.", 200, statusCode);
		
		// Retrieve the body of the Response
		ResponseBody body = response.getBody();
		String bodyString = body.asString();
		
		// create a jsonObject so that we can retrieve data
		JsonObject jsonObject = new JsonParser().parse(bodyString).getAsJsonObject();

		int petId = jsonObject.get("id").getAsInt();
		String petName = jsonObject.get("name").getAsString();
		String petStatus = jsonObject.get("status").getAsString();
		assertEquals("Check petId.", 10, petId);
		assertEquals("Check petName.", "PetName9", petName);
		assertEquals("Check petStatus.", "PetStatus9", petStatus);
	}
	
	@Test
	public void testGet_InvalidId() {
		RestAssured.baseURI = InitializeEnvironment.getWebAppLocation();
		RequestSpecification httpRequest = RestAssured.given();
		Response response = RestHelper.restGet(httpRequest, "/api/pets/600");
		
		Headers headers = response.getHeaders();
		String contentType = headers.getValue("Content-Type");
		String[] contentTypeParts = contentType.split(";");
		assertEquals("application/json", contentTypeParts[0]);
		
		// get status code
		int statusCode = response.getStatusCode();
		assertEquals(404, statusCode);
		
		// Retrieve the body of the Response
		ResponseBody body = response.getBody();
		String bodyString = body.asString();
		
		// create a jsonObject so that we can retrieve data
		JsonObject jsonObject = new JsonParser().parse(bodyString).getAsJsonObject();
		Set<Entry<String, JsonElement>> set = jsonObject.entrySet();
		assertEquals("Check json object is empty.", 0, set.size());
	}
	
	@Test
	public void testGet_All() {
		RestAssured.baseURI = InitializeEnvironment.getWebAppLocation();
		RequestSpecification httpRequest = RestAssured.given();
		Response response = RestHelper.restGet(httpRequest, "/api/pets");
		
		Headers headers = response.getHeaders();
		String contentType = headers.getValue("Content-Type");
		String[] contentTypeParts = contentType.split(";");
		assertEquals("application/json", contentTypeParts[0]);
		
		// get status code
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);
		
		// Retrieve the body of the Response
		ResponseBody body = response.getBody();
		String bodyString = body.asString();
		
		// create a jsonObject so that we can retrieve data
		JsonArray jsonArray = new JsonParser().parse(bodyString).getAsJsonArray();
        for(int i = 0; i < jsonArray.size(); i++){
        		JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
        		int petId = jsonObject.get("id").getAsInt();
        		String petName = jsonObject.get("name").getAsString();
        		String petStatus = jsonObject.get("status").getAsString();
        		assertEquals("Check petId.", i+1, petId);
        		assertEquals("Check petName.", "PetName"+i, petName);
        		assertEquals("Check petStatus.", "PetStatus"+i, petStatus);
        }
	}

	@Test
	public void testDelete_ValidId() {
		RestAssured.baseURI = InitializeEnvironment.getWebAppLocation();
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.delete("/api/pets/2");
		
		Headers headers = response.getHeaders();
		String contentType = headers.getValue("Content-Type");
		String[] contentTypeParts = contentType.split(";");
		assertEquals("application/json", contentTypeParts[0]);
		
		// get status code
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);
		
		// Retrieve the body of the Response
		ResponseBody body = response.getBody();
		String bodyString = body.asString();
		
		// create a jsonObject so that we can retrieve data
		JsonObject jsonObject = new JsonParser().parse(bodyString).getAsJsonObject();
		Set<Entry<String, JsonElement>> set = jsonObject.entrySet();
		assertEquals("Check json object is empty.", 0, set.size());
		
		// try to get the deleted object
		response = RestHelper.restGet(httpRequest, "/api/pets/2");
		
		headers = response.getHeaders();
		contentType = headers.getValue("Content-Type");
		contentTypeParts = contentType.split(";");
		assertEquals("application/json", contentTypeParts[0]);
		
		// get status code
		statusCode = response.getStatusCode();
		assertEquals("Deleted entry should no longer exit.", 404, statusCode);
	}
	
	@Test
	public void testDelete_InvalidId() {
		RestAssured.baseURI = InitializeEnvironment.getWebAppLocation();
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.delete("/api/pets/680");
		
		Headers headers = response.getHeaders();
		String contentType = headers.getValue("Content-Type");
		String[] contentTypeParts = contentType.split(";");
		assertEquals("application/json", contentTypeParts[0]);
		
		// get status code
		int statusCode = response.getStatusCode();
		assertEquals(404, statusCode);
		
		// Retrieve the body of the Response
		ResponseBody body = response.getBody();
		String bodyString = body.asString();
		
		// create a jsonObject so that we can retrieve data
		JsonObject jsonObject = new JsonParser().parse(bodyString).getAsJsonObject();
		Set<Entry<String, JsonElement>> set = jsonObject.entrySet();
		assertEquals("Check json object is empty.", 0, set.size());
	}

	@Test
	public void testPost_NoUriId_WithNoBody() {
		test_WithId_AndBodyParameters("post", -1, -1, null, null, false);
	}
	
	@Test
	public void testPost_NoUriId_WithEmptyBody() {
		test_WithId_AndBodyParameters("post", -1, -1, null, null, false, true);
	}
	
	@Test
	public void testPost_NoUriId_WithBodyContainingExistingId() {
		test_WithId_AndBodyParameters("post", -1, 3, null, null, true);
	}
	
	@Test
	public void testPost_NoUriId_WithBodyContainingNewId() {
		test_WithId_AndBodyParameters("post", -1, 30, null, null, false);
	}
	
	@Test
	public void testPost_NoUriId_WithBodyContainingName() {
		test_WithId_AndBodyParameters("post", -1, -1, "UltraCat", null, false);
	}
	
	@Test
	public void testPost_NoUriId_WithBodyContainingStatus() {
		test_WithId_AndBodyParameters("post", -1, -1, null, "available", false);
	}
	
	@Test
	public void testPost_NoUriId_WithBodyContainingNameAndStatus() {
		test_WithId_AndBodyParameters("post", -1, -1, "UltraCat", "available", false);
	}
	
	@Test
	public void testPost_NoUriId_WithBodyContainingNewIdAndNameAndStatus() {
		test_WithId_AndBodyParameters("post", -1, 30, "UltraCat", "available", false);
	}
	
	
	
	@Test
	public void testPut_WithExistingId_WithBodyContainingName() {
		test_WithId_AndBodyParameters("put", 12, -1, "UltraCat", null, false);
	}
	
	@Test
	public void testPut_WithExistingId_WithBodyContainingStatus() {
		test_WithId_AndBodyParameters("put", 14, -1, null, "outofstock", false);
	}
	
	@Test
	public void testPut_WithExistingId_WithBodyContainingNameAndStatus() {
		test_WithId_AndBodyParameters("put", 13, -1, "UltraCat", "available", false);
	}
	
	@Test
	public void testPut_WithExistingId_WithBodyContainingMatchingIdAndNameAndStatus() {
		test_WithId_AndBodyParameters("put", 13, 13, "UltraCat", "outofstock", false);
	}
	
	@Test
	public void testPut_WithExistingId_WithBodyContainingMismatchedIdAndNameAndStatus() {
		test_WithId_AndBodyParameters("put", 13, 14, "UltraCat", "outofstock", false);
	}
	
	@Test
	public void testPut_WithNewId_WithBodyContainingNameAndStatus() {
		test_WithId_AndBodyParameters("put", 30, -1, "UltraCat", "outofstock", true);
	}
	
	public void test_WithId_AndBodyParameters(String operation, int uriId, int bodyId, String bodyName, String bodyStatus, Boolean errorExpected) {
		test_WithId_AndBodyParameters(operation, uriId, bodyId, bodyName, bodyStatus, errorExpected, false);
	}
	
	public void test_WithId_AndBodyParameters(String operation, int uriId, int bodyId, String bodyName, String bodyStatus, Boolean errorExpected, Boolean forceEmptyBody) {
		RestAssured.baseURI = InitializeEnvironment.getWebAppLocation();
		
		JsonObject jsonObject = null;
		if (forceEmptyBody == true) {
			jsonObject = new JsonObject();
		}
		
		if (bodyId > -1)
		{
			if (jsonObject == null) {
				jsonObject = new JsonObject();
			}
			jsonObject.addProperty("id", bodyId);
		}
		if (bodyName != null) {
			if (jsonObject == null) {
				jsonObject = new JsonObject();
			}
			jsonObject.addProperty("name", bodyName);
		}
		if (bodyStatus != null) {
			if (jsonObject == null) {
				jsonObject = new JsonObject();
			}
			jsonObject.addProperty("status", bodyStatus);
		}
		
		RequestSpecification httpRequest = null;
		if (jsonObject == null) {
			httpRequest = RestAssured.given().contentType(ContentType.JSON);
		}
		else {
			httpRequest = RestAssured.given().body(jsonObject.toString()).contentType(ContentType.JSON);
		}
		Response response = null;
		String suffix = "";
		if (uriId > -1) {
			suffix = "/" + Integer.toString(uriId);
		}
		switch (operation) {
			case "post":
				response = RestHelper.restPost(httpRequest, "/api/pets" + suffix);
				break;
			case "put":
				response = RestHelper.restPut(httpRequest, "/api/pets" + suffix);
				break;
		}
		
		// get status code
		int statusCode = response.getStatusCode();
		switch (operation) {
		case "post":
			if (errorExpected == false) {
				assertEquals("Check that status code 201 (created) is returned.", 201, statusCode);
			}
			else {
				assertEquals("Check that an error code was returned.", 500, statusCode);
				return;
			}
			break;
		case "put":
			if (errorExpected == false) {
				assertEquals("Check that status code 200 (modified) is returned.", 200, statusCode);
			}
			else {
				assertEquals("Check that an error code was returned.", 404, statusCode);
				return;
			}
			break;
		}
		
		Headers headers = response.getHeaders();
		String contentType = headers.getValue("Content-Type");
		String[] contentTypeParts = contentType.split(";");
		assertEquals("application/json", contentTypeParts[0]);
		
		// Retrieve the body of the Response
		ResponseBody body = response.getBody();
		String bodyString = body.asString();
		
		// create a jsonObject so that we can retrieve data
		JsonObject jsonReplyObject = new JsonParser().parse(bodyString).getAsJsonObject();

		int petId = jsonReplyObject.get("id").getAsInt();
		switch (operation) {
			case "post":
				if (bodyId > -1) {
					assertEquals("Check that the expected pet id is created.", bodyId, petId);
				}
				else {
					assertEquals("Check that the expected pet id is created - the next available spot in db.", 21, petId);
				}
				break;
			case "put":
				assertEquals("Check that the pet id in the body matches the one passed in the uriId.", uriId, petId);
				break;
		}
		
		JsonElement petNameElement = jsonReplyObject.get("name");
		if (bodyName != null) {
			String petName = petNameElement.getAsString();
			assertEquals("Check that the expected pet name is created.", bodyName, petName);
		}
		else {
			assertTrue("Check that there is no pet name element.", petNameElement == null);
		}
		
		JsonElement petStatusElement = jsonReplyObject.get("status");
		if (bodyStatus != null) {
			String petStatus = petStatusElement.getAsString();
			assertEquals("Check that the expected pet name is created.", bodyStatus, petStatus);
		}
		else {
			assertTrue("Check that there is no pet status element.", petStatusElement == null);
		}
	}

}
